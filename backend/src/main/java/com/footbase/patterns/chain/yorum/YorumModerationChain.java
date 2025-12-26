package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.HandlerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        logger.info("⛓️ YorumModerationChain oluşturuldu");
    }
    
    @jakarta.annotation.PostConstruct
    public void buildChain() {
        logger.info("🔗 Yorum moderasyon zinciri kuruluyor...");
        
        kufurFiltresi.setNext(spamKontrol)
                     .setNext(uzunlukKontrol)
                     .setNext(linkKontrol);
        
        chain = kufurFiltresi;
        
        logger.info("✅ Zincir kuruldu: {}", chain.visualizeChain());
    }
    
    public HandlerResult moderate(Yorum yorum) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("💬 YORUM MODERASYONU BAŞLIYOR");
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("Yorum: \"{}\"", yorum.getMesaj());
        logger.info("Kullanıcı: {}", yorum.getKullanici() != null ? yorum.getKullanici().getEmail() : "Bilinmiyor");
        logger.info("───────────────────────────────────────────────────────");
        
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
    
    public boolean quickCheck(Yorum yorum) {
        return chain.handle(yorum).isSuccess();
    }
    
    public String getChainVisualization() {
        return chain.visualizeChain();
    }
}
