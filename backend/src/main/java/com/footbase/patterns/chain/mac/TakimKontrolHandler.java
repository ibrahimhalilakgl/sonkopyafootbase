package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.entity.MacTakimlari;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TakimKontrolHandler extends MacOnayHandler {
    
    public TakimKontrolHandler() {
        this.priority = 2;
        logger.info("👥 TakimKontrolHandler oluşturuldu");
    }
    
    @Override
    protected HandlerResult doHandle(Mac mac) {
        List<MacTakimlari> takimlar = mac.getMacTakimlari();
        
        if (takimlar == null || takimlar.isEmpty()) {
            return HandlerResult.failure("Maç için takımlar belirtilmelidir", getHandlerName());
        }
        
        if (takimlar.size() < 2) {
            return HandlerResult.failure("Maç için en az 2 takım gereklidir", getHandlerName());
        }
        
        boolean evSahibiVar = false;
        boolean deplasmanVar = false;
        
        for (MacTakimlari mt : takimlar) {
            if (mt.getTakim() == null) {
                return HandlerResult.failure("Takım bilgisi eksik", getHandlerName());
            }
            
            if (Boolean.TRUE.equals(mt.getEvSahibi())) {
                evSahibiVar = true;
            } else if (Boolean.FALSE.equals(mt.getEvSahibi())) {
                deplasmanVar = true;
            }
        }
        
        if (!evSahibiVar) {
            return HandlerResult.failure("Ev sahibi takım belirtilmelidir", getHandlerName());
        }
        
        if (!deplasmanVar) {
            return HandlerResult.failure("Deplasman takımı belirtilmelidir", getHandlerName());
        }
        
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
