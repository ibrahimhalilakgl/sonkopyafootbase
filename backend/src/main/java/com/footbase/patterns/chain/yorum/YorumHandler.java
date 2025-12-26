package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.Handler;

/**
 * Yorum Handler (Abstract)
 * 
 * Yorum moderasyon zincirinin temel sınıfı.
 * Tüm yorum kontrol handler'ları bu sınıfı extend eder.
 * 
 * YORUM MODERASYON ZİNCİRİ:
 * 1. Küfür Filtresi
 * 2. Spam Kontrolü
 * 3. Uzunluk Kontrolü
 * 4. Link Kontrolü
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public abstract class YorumHandler extends Handler<Yorum> {
    
    /**
     * Yorum handler'ı için özel log formatı
     * 
     * @param yorum İşlenen yorum
     * @param action Yapılan işlem
     */
    protected void logYorumAction(Yorum yorum, String action) {
        logger.info("💬 [{}] Yorum ID: {}, İşlem: {}", 
                   getHandlerName(), 
                   yorum.getId() != null ? yorum.getId() : "YENİ", 
                   action);
    }
}

