package com.footbase.patterns.command;

import com.footbase.entity.MacTakimlari;
import com.footbase.patterns.command.dto.SkorGirisiDTO;
import com.footbase.repository.MacRepository;
import com.footbase.repository.MacTakimlariRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command Pattern - Concrete Command
 * Skor Girişi Komutu
 * 
 * Maça skor girer ve işlemi geri alabilir (undo)
 */
public class SkorGirisiCommand extends MacCommand {
    
    private final MacRepository macRepository;
    private final MacTakimlariRepository macTakimlariRepository;
    private final SkorGirisiDTO skorGirisiDTO;
    
    // Undo için önceki skorları sakla
    private Map<Long, Integer> oncekiSkorlar = new HashMap<>();
    
    public SkorGirisiCommand(
            SkorGirisiDTO skorGirisiDTO,
            Long kullaniciId,
            MacRepository macRepository,
            MacTakimlariRepository macTakimlariRepository) {
        super(kullaniciId);
        this.skorGirisiDTO = skorGirisiDTO;
        this.macRepository = macRepository;
        this.macTakimlariRepository = macTakimlariRepository;
    }
    
    @Override
    protected boolean doExecute() {
        try {
            // Maçı kontrol et
            macRepository.findById(skorGirisiDTO.getMacId())
                    .orElseThrow(() -> new RuntimeException("Maç bulunamadı: " + skorGirisiDTO.getMacId()));
            
            // Maç takımlarını getir
            List<MacTakimlari> macTakimlari = macTakimlariRepository.findByMacId(skorGirisiDTO.getMacId());
            
            if (macTakimlari.size() != 2) {
                throw new RuntimeException("Maçta tam 2 takım olmalı!");
            }
            
            // Önceki skorları kaydet (undo için)
            for (MacTakimlari mt : macTakimlari) {
                oncekiSkorlar.put(mt.getId(), mt.getSkor());
            }
            
            // Yeni skorları güncelle
            for (MacTakimlari mt : macTakimlari) {
                if (mt.getEvSahibi()) {
                    mt.setSkor(skorGirisiDTO.getEvSahibiSkor());
                    logger.info("📊 Ev sahibi skor güncellendi: {} → {}", 
                            oncekiSkorlar.get(mt.getId()), 
                            skorGirisiDTO.getEvSahibiSkor());
                } else {
                    mt.setSkor(skorGirisiDTO.getDeplasmanSkor());
                    logger.info("📊 Deplasman skor güncellendi: {} → {}", 
                            oncekiSkorlar.get(mt.getId()), 
                            skorGirisiDTO.getDeplasmanSkor());
                }
                macTakimlariRepository.save(mt);
            }
            
            logger.info("⚽ Skor girişi tamamlandı: {} - {}", 
                    skorGirisiDTO.getEvSahibiSkor(), 
                    skorGirisiDTO.getDeplasmanSkor());
            
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Skor girişi hatası: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    protected boolean doUndo() {
        try {
            // Önceki skorları geri yükle
            for (Map.Entry<Long, Integer> entry : oncekiSkorlar.entrySet()) {
                Long macTakimlariId = entry.getKey();
                if (macTakimlariId == null) {
                    continue;
                }
                MacTakimlari mt = macTakimlariRepository.findById(macTakimlariId)
                        .orElseThrow(() -> new RuntimeException("MacTakimlari bulunamadı: " + macTakimlariId));
                
                Integer oncekiSkor = entry.getValue();
                mt.setSkor(oncekiSkor);
                macTakimlariRepository.save(mt);
                
                logger.info("🔄 Skor geri alındı: {} (ID: {})", oncekiSkor, mt.getId());
            }
            
            logger.info("✅ Skor girişi başarıyla geri alındı!");
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Skor geri alma hatası: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Skor Girişi: Maç #%d - %d:%d (Kullanıcı: %d)", 
                skorGirisiDTO.getMacId(),
                skorGirisiDTO.getEvSahibiSkor(),
                skorGirisiDTO.getDeplasmanSkor(),
                kullaniciId);
    }
    
    @Override
    public String getCommandType() {
        return "SKOR_GIRISI";
    }
}

