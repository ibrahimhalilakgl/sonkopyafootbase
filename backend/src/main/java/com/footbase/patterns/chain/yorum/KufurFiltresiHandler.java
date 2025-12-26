package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Küfür Filtresi Handler
 * 
 * Yorumlardaki küfürlü içeriği filtreler.
 * Zincirin ilk basamağı (en yüksek öncelik).
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class KufurFiltresiHandler extends YorumHandler {
    
    /**
     * Yasaklı kelimeler listesi (örnek)
     * Gerçek uygulamada veritabanından veya config dosyasından okunabilir
     */
    private static final List<String> YASAKLI_KELIMELER = Arrays.asList(
        "küfür1", "küfür2", "küfür3", // Gerçek küfürleri buraya ekleyin
        "aptal", "salak", "gerizekalı"
    );
    
    public KufurFiltresiHandler() {
        this.priority = 1; // En yüksek öncelik
        logger.info("🚫 KufurFiltresiHandler oluşturuldu");
    }
    
    @Override
    protected HandlerResult doHandle(Yorum yorum) {
        String mesaj = yorum.getMesaj();
        
        if (mesaj == null || mesaj.trim().isEmpty()) {
            return HandlerResult.failure("Yorum mesajı boş olamaz", getHandlerName());
        }
        
        String mesajLower = mesaj.toLowerCase();
        
        // Küfür kontrolü
        for (String yasakliKelime : YASAKLI_KELIMELER) {
            if (mesajLower.contains(yasakliKelime.toLowerCase())) {
                logYorumAction(yorum, "KÜFÜR TESPİT EDİLDİ: " + yasakliKelime);
                return HandlerResult.failure(
                    "Yorumunuz uygunsuz içerik barındırıyor. Lütfen düzenleyiniz.",
                    getHandlerName()
                );
            }
        }
        
        logYorumAction(yorum, "Küfür kontrolü BAŞARILI");
        return HandlerResult.success();
    }
    
    /**
     * Küfür kelimelerini maskeler (opsiyonel özellik)
     * 
     * @param mesaj Orijinal mesaj
     * @return Maskelenmiş mesaj
     */
    public String maskProfanity(String mesaj) {
        String masked = mesaj;
        
        for (String yasakliKelime : YASAKLI_KELIMELER) {
            if (masked.toLowerCase().contains(yasakliKelime.toLowerCase())) {
                String mask = "*".repeat(yasakliKelime.length());
                masked = masked.replaceAll("(?i)" + yasakliKelime, mask);
            }
        }
        
        return masked;
    }
}

