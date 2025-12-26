package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

/**
 * Uzunluk Kontrol Handler
 * 
 * Yorum uzunluğunu kontrol eder:
 * - Minimum uzunluk kontrolü
 * - Maksimum uzunluk kontrolü
 * - Boşluk karakteri kontrolü
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class UzunlukKontrolHandler extends YorumHandler {
    
    /**
     * Minimum yorum uzunluğu
     */
    private static final int MIN_UZUNLUK = 3;
    
    /**
     * Maksimum yorum uzunluğu
     */
    private static final int MAX_UZUNLUK = 500;
    
    public UzunlukKontrolHandler() {
        this.priority = 3; // Üçüncü öncelik
        logger.info("📏 UzunlukKontrolHandler oluşturuldu");
    }
    
    @Override
    protected HandlerResult doHandle(Yorum yorum) {
        String mesaj = yorum.getMesaj();
        
        if (mesaj == null) {
            return HandlerResult.failure("Yorum mesajı boş olamaz", getHandlerName());
        }
        
        String mesajTrimmed = mesaj.trim();
        int uzunluk = mesajTrimmed.length();
        
        // Minimum uzunluk kontrolü
        if (uzunluk < MIN_UZUNLUK) {
            logYorumAction(yorum, String.format("UZUNLUK HATASI: %d karakter (min: %d)", uzunluk, MIN_UZUNLUK));
            return HandlerResult.failure(
                String.format("Yorum en az %d karakter olmalıdır", MIN_UZUNLUK),
                getHandlerName()
            );
        }
        
        // Maksimum uzunluk kontrolü
        if (uzunluk > MAX_UZUNLUK) {
            logYorumAction(yorum, String.format("UZUNLUK HATASI: %d karakter (max: %d)", uzunluk, MAX_UZUNLUK));
            return HandlerResult.failure(
                String.format("Yorum en fazla %d karakter olabilir", MAX_UZUNLUK),
                getHandlerName()
            );
        }
        
        // Sadece boşluk karakteri kontrolü
        if (mesajTrimmed.isEmpty() || mesajTrimmed.chars().allMatch(Character::isWhitespace)) {
            logYorumAction(yorum, "HATA: Sadece boşluk karakteri");
            return HandlerResult.failure(
                "Yorum sadece boşluk karakteri içeremez",
                getHandlerName()
            );
        }
        
        logYorumAction(yorum, String.format("Uzunluk kontrolü BAŞARILI (%d karakter)", uzunluk));
        return HandlerResult.success();
    }
    
    /**
     * Minimum uzunluk bilgisini döndürür
     * 
     * @return Minimum uzunluk
     */
    public int getMinUzunluk() {
        return MIN_UZUNLUK;
    }
    
    /**
     * Maksimum uzunluk bilgisini döndürür
     * 
     * @return Maksimum uzunluk
     */
    public int getMaxUzunluk() {
        return MAX_UZUNLUK;
    }
}

