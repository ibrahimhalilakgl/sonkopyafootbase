package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.patterns.chain.Handler;

/**
 * Maç Onay Handler (Abstract)
 * 
 * Maç onay zincirinin temel sınıfı.
 * Tüm maç kontrol handler'ları bu sınıfı extend eder.
 * 
 * MAÇ ONAY ZİNCİRİ:
 * 1. Tarih Kontrolü
 * 2. Takım Kontrolü
 * 3. Saat Kontrolü
 * 4. Stadyum Kontrolü
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public abstract class MacOnayHandler extends Handler<Mac> {
    
    /**
     * Maç handler'ı için özel log formatı
     * 
     * @param mac İşlenen maç
     * @param action Yapılan işlem
     */
    protected void logMacAction(Mac mac, String action) {
        logger.info("⚽ [{}] Maç ID: {}, İşlem: {}", 
                   getHandlerName(), 
                   mac.getId() != null ? mac.getId() : "YENİ", 
                   action);
    }
}

