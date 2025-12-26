package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class SaatKontrolHandler extends MacOnayHandler {
    
    private static final LocalTime EN_ERKEN_SAAT = LocalTime.of(10, 0);
    private static final LocalTime EN_GEC_SAAT = LocalTime.of(23, 0);
    
    public SaatKontrolHandler() {
        this.priority = 3;
        logger.info("🕐 SaatKontrolHandler oluşturuldu");
    }
    
    @Override
    protected HandlerResult doHandle(Mac mac) {
        LocalTime saat = mac.getSaat();
        
        if (saat == null) {
            return HandlerResult.failure("Maç saati belirtilmelidir", getHandlerName());
        }
        
        if (saat.isBefore(EN_ERKEN_SAAT)) {
            logMacAction(mac, String.format("UYARI: Erken saat - %s (önerilen: %s sonrası)", 
                                           saat, EN_ERKEN_SAAT));
        }
        
        if (saat.isAfter(EN_GEC_SAAT)) {
            logMacAction(mac, String.format("UYARI: Geç saat - %s (önerilen: %s öncesi)", 
                                           saat, EN_GEC_SAAT));
        }
        
        logMacAction(mac, String.format("Saat kontrolü BAŞARILI (%s)", saat));
        return HandlerResult.success();
    }
}
