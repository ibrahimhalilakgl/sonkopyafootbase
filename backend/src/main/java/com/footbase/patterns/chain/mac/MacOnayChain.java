package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.patterns.chain.HandlerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Maç Onay Zinciri (Chain Manager)
 * 
 * Maç onay handler'larını yönetir ve zinciri kurar.
 * Chain of Responsibility Pattern'in Context sınıfı.
 * 
 * ONAY ZİNCİRİ:
 * Maç → Tarih → Takım → Saat → Stadyum → ✅
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class MacOnayChain {
    
    private static final Logger logger = LoggerFactory.getLogger(MacOnayChain.class);
    
    @Autowired
    private TarihKontrolHandler tarihKontrol;
    
    @Autowired
    private TakimKontrolHandler takimKontrol;
    
    @Autowired
    private SaatKontrolHandler saatKontrol;
    
    @Autowired
    private StadyumKontrolHandler stadyumKontrol;
    
    private MacOnayHandler chain;
    
    public MacOnayChain() {
        logger.info("⛓️ MacOnayChain oluşturuldu (Chain of Responsibility Pattern)");
    }
    
    /**
     * Zinciri kurar (PostConstruct ile otomatik)
     */
    @jakarta.annotation.PostConstruct
    public void buildChain() {
        logger.info("🔗 Maç onay zinciri kuruluyor...");
        
        // Zinciri kur: Tarih → Takım → Saat → Stadyum
        tarihKontrol.setNext(takimKontrol)
                    .setNext(saatKontrol)
                    .setNext(stadyumKontrol);
        
        chain = tarihKontrol;
        
        logger.info("✅ Zincir kuruldu: {}", chain.visualizeChain());
    }
    
    /**
     * Maçı onay zincirinden geçirir
     * 
     * @param mac Kontrol edilecek maç
     * @return Onay sonucu
     */
    public HandlerResult validate(Mac mac) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("⚽ MAÇ ONAY SÜRECİ BAŞLIYOR");
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("Tarih: {} {}", mac.getTarih(), mac.getSaat());
        logger.info("Stadyum: {}", mac.getStadyum() != null ? mac.getStadyum() : "Belirtilmemiş");
        logger.info("───────────────────────────────────────────────────────");
        
        // Zinciri çalıştır
        HandlerResult result = chain.handle(mac);
        
        logger.info("───────────────────────────────────────────────────────");
        if (result.isSuccess()) {
            logger.info("✅ ONAY BAŞARILI - Maç onaylandı");
        } else {
            logger.warn("❌ ONAY BAŞARISIZ - {}", result.getMessage());
        }
        logger.info("═══════════════════════════════════════════════════════\n");
        
        return result;
    }
    
    /**
     * Hızlı kontrol - sadece sonuç döndürür (log'suz)
     * 
     * @param mac Kontrol edilecek maç
     * @return Başarılı ise true
     */
    public boolean quickValidate(Mac mac) {
        return chain.handle(mac).isSuccess();
    }
    
    /**
     * Zinciri görselleştirir
     * 
     * @return Zincir görselleştirmesi
     */
    public String getChainVisualization() {
        return chain.visualizeChain();
    }
}

