package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.HandlerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yorum Moderasyon Zinciri (Chain Manager)
 * 
 * Yorum moderasyon handler'larını yönetir ve zinciri kurar.
 * Chain of Responsibility Pattern'in Context sınıfı.
 * 
 * MODERASYON ZİNCİRİ:
 * Yorum → Küfür Filtresi → Spam Kontrolü → Uzunluk Kontrolü → Link Kontrolü → ✅
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class YorumModerationChain {
    
    private static final Logger logger = LoggerFactory.getLogger(YorumModerationChain.class);
    
    @Autowired
    private KufurFiltresiHandler kufurFiltresi;
    
    @Autowired
    private SpamKontrolHandler spamKontrol;
    
    @Autowired
    private UzunlukKontrolHandler uzunlukKontrol;
    
    @Autowired
    private LinkKontrolHandler linkKontrol;
    
    private YorumHandler chain;
    
    public YorumModerationChain() {
        logger.info("⛓️ YorumModerationChain oluşturuldu (Chain of Responsibility Pattern)");
    }
    
    /**
     * Zinciri kurar (PostConstruct ile otomatik)
     */
    @jakarta.annotation.PostConstruct
    public void buildChain() {
        logger.info("🔗 Yorum moderasyon zinciri kuruluyor...");
        
        // Zinciri kur: Küfür → Spam → Uzunluk → Link
        kufurFiltresi.setNext(spamKontrol)
                     .setNext(uzunlukKontrol)
                     .setNext(linkKontrol);
        
        chain = kufurFiltresi;
        
        logger.info("✅ Zincir kuruldu: {}", chain.visualizeChain());
    }
    
    /**
     * Yorumu moderasyon zincirinden geçirir
     * 
     * @param yorum Kontrol edilecek yorum
     * @return Moderasyon sonucu
     */
    public HandlerResult moderate(Yorum yorum) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("💬 YORUM MODERASYONU BAŞLIYOR");
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("Yorum: \"{}\"", yorum.getMesaj());
        logger.info("Kullanıcı: {}", yorum.getKullanici() != null ? yorum.getKullanici().getEmail() : "Bilinmiyor");
        logger.info("───────────────────────────────────────────────────────");
        
        // Zinciri çalıştır
        HandlerResult result = chain.handle(yorum);
        
        logger.info("───────────────────────────────────────────────────────");
        if (result.isSuccess()) {
            logger.info("✅ MODERASYON BAŞARILI - Yorum onaylandı");
        } else {
            logger.warn("❌ MODERASYON BAŞARISIZ - {}", result.getMessage());
        }
        logger.info("═══════════════════════════════════════════════════════\n");
        
        return result;
    }
    
    /**
     * Hızlı kontrol - sadece sonuç döndürür (log'suz)
     * 
     * @param yorum Kontrol edilecek yorum
     * @return Başarılı ise true
     */
    public boolean quickCheck(Yorum yorum) {
        return chain.handle(yorum).isSuccess();
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

