package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Saat Kontrol Handler
 * 
 * Maç saatini kontrol eder:
 * - Geçerli saat aralığı (00:00 - 23:59)
 * - Makul oynanma saatleri
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class SaatKontrolHandler extends MacOnayHandler {
    
    /**
     * Önerilen en erken maç saati
     */
    private static final LocalTime EN_ERKEN_SAAT = LocalTime.of(10, 0);
    
    /**
     * Önerilen en geç maç saati
     */
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
        
        // Saat aralığı kontrolü (warning - engelleme değil)
        if (saat.isBefore(EN_ERKEN_SAAT)) {
            logMacAction(mac, String.format("UYARI: Erken saat - %s (önerilen: %s sonrası)", 
                                           saat, EN_ERKEN_SAAT));
            // Warning - engelleme değil
        }
        
        if (saat.isAfter(EN_GEC_SAAT)) {
            logMacAction(mac, String.format("UYARI: Geç saat - %s (önerilen: %s öncesi)", 
                                           saat, EN_GEC_SAAT));
            // Warning - engelleme değil
        }
        
        logMacAction(mac, String.format("Saat kontrolü BAŞARILI (%s)", saat));
        return HandlerResult.success();
    }
}

