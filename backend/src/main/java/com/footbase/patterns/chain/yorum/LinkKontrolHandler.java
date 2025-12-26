package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Link Kontrol Handler
 * 
 * Yorumlardaki linkleri kontrol eder:
 * - Spam link tespiti
 * - Güvenli olmayan linkler
 * - Maksimum link sayısı
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class LinkKontrolHandler extends YorumHandler {
    
    /**
     * URL regex pattern
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
        "((https?|ftp)://|(www\\.))[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Maksimum izin verilen link sayısı
     */
    private static final int MAX_LINK_SAYISI = 2;
    
    /**
     * Link'e izin verilsin mi? (false: linkler engellenir)
     */
    private boolean linkIzinVer = true;
    
    public LinkKontrolHandler() {
        this.priority = 4; // Dördüncü öncelik
        logger.info("🔗 LinkKontrolHandler oluşturuldu");
    }
    
    @Override
    protected HandlerResult doHandle(Yorum yorum) {
        String mesaj = yorum.getMesaj();
        
        if (mesaj == null) {
            return HandlerResult.success();
        }
        
        // Link sayısını say
        Matcher matcher = URL_PATTERN.matcher(mesaj);
        int linkSayisi = 0;
        
        while (matcher.find()) {
            linkSayisi++;
        }
        
        // Link varsa
        if (linkSayisi > 0) {
            logYorumAction(yorum, String.format("Link tespit edildi: %d adet", linkSayisi));
            
            // Link'e izin verilmiyor mu?
            if (!linkIzinVer) {
                logYorumAction(yorum, "ENGELLENDI: Link'e izin yok");
                return HandlerResult.failure(
                    "Yorumlarda link paylaşımı yasaktır",
                    getHandlerName()
                );
            }
            
            // Maksimum link sayısı kontrolü
            if (linkSayisi > MAX_LINK_SAYISI) {
                logYorumAction(yorum, String.format("ENGELLENDI: Çok fazla link (%d > %d)", 
                                                    linkSayisi, MAX_LINK_SAYISI));
                return HandlerResult.failure(
                    String.format("En fazla %d link paylaşabilirsiniz", MAX_LINK_SAYISI),
                    getHandlerName()
                );
            }
            
            logYorumAction(yorum, "Link kontrolü BAŞARILI");
        }
        
        return HandlerResult.success();
    }
    
    /**
     * Link izin durumunu ayarlar
     * 
     * @param izinVer true: izin ver, false: engelle
     */
    public void setLinkIzinVer(boolean izinVer) {
        this.linkIzinVer = izinVer;
        logger.info("Link izin durumu değiştirildi: {}", izinVer ? "İZİN VERİLDİ" : "ENGELLENDİ");
    }
    
    /**
     * Mesajdaki linkleri temizler
     * 
     * @param mesaj Orijinal mesaj
     * @return Linkler temizlenmiş mesaj
     */
    public String removeLinkler(String mesaj) {
        return URL_PATTERN.matcher(mesaj).replaceAll("[Link Kaldırıldı]");
    }
}

