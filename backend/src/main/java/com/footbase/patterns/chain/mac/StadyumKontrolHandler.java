package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

@Component
public class StadyumKontrolHandler extends MacOnayHandler {
    
    public StadyumKontrolHandler() {
        this.priority = 4;
        logger.info("🏟️ StadyumKontrolHandler oluşturuldu");
    }
    
    @Override
    protected HandlerResult doHandle(Mac mac) {
        com.footbase.entity.Stadyum stadyum = mac.getStadyum();
        
        if (stadyum == null) {
            logMacAction(mac, "UYARI: Stadyum bilgisi yok");
            return HandlerResult.success();
        }
        
        String stadyumAdi = stadyum.getStadyumAdi();
        
        if (stadyumAdi == null || stadyumAdi.trim().isEmpty()) {
            logMacAction(mac, "UYARI: Stadyum adı yok");
            return HandlerResult.success();
        }
        
        if (stadyumAdi.trim().length() < 3) {
            return HandlerResult.failure("Stadyum adı en az 3 karakter olmalıdır", getHandlerName());
        }
        
        if (stadyumAdi.length() > 100) {
            return HandlerResult.failure("Stadyum adı en fazla 100 karakter olabilir", getHandlerName());
        }
        
        logMacAction(mac, String.format("Stadyum kontrolü BAŞARILI (%s)", stadyumAdi));
        return HandlerResult.success();
    }
}
