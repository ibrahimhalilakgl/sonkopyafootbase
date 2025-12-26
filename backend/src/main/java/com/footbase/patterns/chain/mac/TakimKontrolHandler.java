package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.entity.MacTakimlari;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Takım Kontrol Handler
 * 
 * Maç takımlarını kontrol eder:
 * - En az 2 takım olmalı
 * - Takımlar farklı olmalı
 * - Ev sahibi ve deplasman belirlenmeli
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class TakimKontrolHandler extends MacOnayHandler {
    
    public TakimKontrolHandler() {
        this.priority = 2;
        logger.info("👥 TakimKontrolHandler oluşturuldu");
    }
    
    @Override
    protected HandlerResult doHandle(Mac mac) {
        List<MacTakimlari> takimlar = mac.getMacTakimlari();
        
        // Takım sayısı kontrolü
        if (takimlar == null || takimlar.isEmpty()) {
            return HandlerResult.failure(
                "Maç için takımlar belirtilmelidir",
                getHandlerName()
            );
        }
        
        if (takimlar.size() < 2) {
            return HandlerResult.failure(
                "Maç için en az 2 takım gereklidir",
                getHandlerName()
            );
        }
        
        // Ev sahibi ve deplasman kontrolü
        boolean evSahibiVar = false;
        boolean deplasmanVar = false;
        
        for (MacTakimlari mt : takimlar) {
            if (mt.getTakim() == null) {
                return HandlerResult.failure(
                    "Takım bilgisi eksik",
                    getHandlerName()
                );
            }
            
            // evSahibi Boolean field'ı kullan
            if (Boolean.TRUE.equals(mt.getEvSahibi())) {
                evSahibiVar = true;
            } else if (Boolean.FALSE.equals(mt.getEvSahibi())) {
                deplasmanVar = true;
            }
        }
        
        if (!evSahibiVar) {
            return HandlerResult.failure(
                "Ev sahibi takım belirtilmelidir",
                getHandlerName()
            );
        }
        
        if (!deplasmanVar) {
            return HandlerResult.failure(
                "Deplasman takımı belirtilmelidir",
                getHandlerName()
            );
        }
        
        // Aynı takım kontrolü
        if (takimlar.size() >= 2) {
            Long takim1Id = takimlar.get(0).getTakim().getId();
            Long takim2Id = takimlar.get(1).getTakim().getId();
            
            if (takim1Id != null && takim1Id.equals(takim2Id)) {
                return HandlerResult.failure(
                    "Aynı takım hem ev sahibi hem deplasman olamaz",
                    getHandlerName()
                );
            }
        }
        
        logMacAction(mac, String.format("Takım kontrolü BAŞARILI (%d takım)", takimlar.size()));
        return HandlerResult.success();
    }
}

