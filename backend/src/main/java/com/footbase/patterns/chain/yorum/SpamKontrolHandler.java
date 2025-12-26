package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Spam Kontrol Handler
 * 
 * Yorumlardaki spam davranışını tespit eder:
 * - Aynı mesajın tekrar gönderilmesi
 * - Çok hızlı art arda yorum yapılması
 * - Çok fazla tekrarlayan karakterler
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class SpamKontrolHandler extends YorumHandler {
    
    /**
     * Kullanıcı son yorum zamanları (cache)
     * Gerçek uygulamada Redis veya veritabanı kullanılabilir
     */
    private static final Map<Long, LocalDateTime> SON_YORUM_ZAMANLARI = new HashMap<>();
    
    /**
     * Minimum yorum aralığı (saniye)
     */
    private static final int MINIMUM_YORUM_ARALIGI_SANIYE = 10;
    
    /**
     * Maksimum tekrar eden karakter sayısı
     */
    private static final int MAX_TEKRAR_KARAKTER = 5;
    
    public SpamKontrolHandler() {
        this.priority = 2; // İkinci öncelik
        logger.info("🚨 SpamKontrolHandler oluşturuldu");
    }
    
    @Override
    protected HandlerResult doHandle(Yorum yorum) {
        String mesaj = yorum.getMesaj();
        Long kullaniciId = yorum.getKullanici() != null ? yorum.getKullanici().getId() : null;
        
        // 1. Hız kontrolü (çok hızlı yorum)
        if (kullaniciId != null) {
            LocalDateTime sonYorum = SON_YORUM_ZAMANLARI.get(kullaniciId);
            if (sonYorum != null) {
                long saniyeFarki = java.time.Duration.between(sonYorum, LocalDateTime.now()).getSeconds();
                
                if (saniyeFarki < MINIMUM_YORUM_ARALIGI_SANIYE) {
                    logYorumAction(yorum, "SPAM TESPİT EDİLDİ: Çok hızlı yorum");
                    return HandlerResult.failure(
                        String.format("Lütfen %d saniye bekleyiniz", MINIMUM_YORUM_ARALIGI_SANIYE - saniyeFarki),
                        getHandlerName()
                    );
                }
            }
            
            // Son yorum zamanını güncelle
            SON_YORUM_ZAMANLARI.put(kullaniciId, LocalDateTime.now());
        }
        
        // 2. Tekrar eden karakter kontrolü (örn: "aaaaaaaaaa", "!!!!!!!!!")
        if (cokTekrarEdenKarakterVar(mesaj)) {
            logYorumAction(yorum, "SPAM TESPİT EDİLDİ: Çok fazla tekrar eden karakter");
            return HandlerResult.failure(
                "Yorumunuzda çok fazla tekrar eden karakter var",
                getHandlerName()
            );
        }
        
        // 3. Tamamen büyük harf kontrolü
        if (tumunuBuyukHarf(mesaj)) {
            logYorumAction(yorum, "UYARI: Tamamen büyük harf");
            // Bu durum warning olabilir, engellenmeyebilir
        }
        
        logYorumAction(yorum, "Spam kontrolü BAŞARILI");
        return HandlerResult.success();
    }
    
    /**
     * Çok fazla tekrar eden karakter var mı kontrol eder
     * 
     * @param mesaj Mesaj
     * @return Var ise true
     */
    private boolean cokTekrarEdenKarakterVar(String mesaj) {
        if (mesaj == null || mesaj.length() < MAX_TEKRAR_KARAKTER) {
            return false;
        }
        
        int tekrarSayisi = 1;
        char oncekiKarakter = mesaj.charAt(0);
        
        for (int i = 1; i < mesaj.length(); i++) {
            char mevcutKarakter = mesaj.charAt(i);
            
            if (mevcutKarakter == oncekiKarakter) {
                tekrarSayisi++;
                if (tekrarSayisi > MAX_TEKRAR_KARAKTER) {
                    return true;
                }
            } else {
                tekrarSayisi = 1;
                oncekiKarakter = mevcutKarakter;
            }
        }
        
        return false;
    }
    
    /**
     * Tamamen büyük harf mi kontrol eder
     * 
     * @param mesaj Mesaj
     * @return Tamamen büyük harf ise true
     */
    private boolean tumunuBuyukHarf(String mesaj) {
        if (mesaj == null || mesaj.length() < 10) {
            return false; // Kısa mesajlar için kontrol etme
        }
        
        long buyukHarfSayisi = mesaj.chars()
            .filter(Character::isUpperCase)
            .count();
        
        long harfSayisi = mesaj.chars()
            .filter(Character::isLetter)
            .count();
        
        // Harflerin %80'inden fazlası büyük harf mi?
        return harfSayisi > 0 && (buyukHarfSayisi * 100.0 / harfSayisi) > 80;
    }
    
    /**
     * Kullanıcının spam geçmişini temizler (test için)
     * 
     * @param kullaniciId Kullanıcı ID
     */
    public void clearSpamHistory(Long kullaniciId) {
        SON_YORUM_ZAMANLARI.remove(kullaniciId);
    }
}

