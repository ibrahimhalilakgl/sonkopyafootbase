package com.footbase.patterns.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler (Chain of Responsibility Pattern)
 * 
 * Sorumluluk zincirinin temel soyut sınıfı.
 * Her handler bir sonraki handler'a referans tutar.
 * 
 * CHAIN OF RESPONSIBILITY PATTERN:
 * - İsteği işleyecek nesneyi runtime'da belirler
 * - Gönderici ve alıcıyı ayırır
 * - Birden fazla nesne isteği işleme şansı verir
 * 
 * KULLANIM ALANLARI:
 * - Yorum moderasyonu (küfür, spam, uzunluk kontrolü)
 * - Maç onay süreci (validasyon, kontrol, onay)
 * - İstek filtreleme (security, logging, cache)
 * 
 * @param <T> İşlenecek nesne tipi
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public abstract class Handler<T> {
    
    protected static final Logger logger = LoggerFactory.getLogger(Handler.class);
    
    /**
     * Zincirdeki bir sonraki handler
     */
    protected Handler<T> next;
    
    /**
     * Handler'ın öncelik seviyesi (düşük = önce çalışır)
     */
    protected int priority = 0;
    
    /**
     * Bir sonraki handler'ı ayarlar
     * 
     * @param next Sonraki handler
     * @return Bu handler (method chaining için)
     */
    public Handler<T> setNext(Handler<T> next) {
        this.next = next;
        return next;
    }
    
    /**
     * İsteği işler (Template Method Pattern ile)
     * 
     * @param request İşlenecek istek
     * @return İşlem sonucu (başarılı ise true)
     */
    public final HandlerResult handle(T request) {
        logger.debug("🔗 [{}] işleniyor...", this.getClass().getSimpleName());
        
        // Bu handler'ın kontrolünü yap
        HandlerResult result = doHandle(request);
        
        if (!result.isSuccess()) {
            logger.warn("❌ [{}] başarısız: {}", this.getClass().getSimpleName(), result.getMessage());
            return result;
        }
        
        logger.debug("✅ [{}] başarılı", this.getClass().getSimpleName());
        
        // Zincir bitmedi mi? Devam et
        if (next != null) {
            return next.handle(request);
        }
        
        // Zincir bitti, başarılı
        return HandlerResult.success("Tüm kontroller başarılı");
    }
    
    /**
     * Handler'ın kendi kontrolünü yapar (abstract)
     * Alt sınıflar bu metodu implement etmeli
     * 
     * @param request İşlenecek istek
     * @return İşlem sonucu
     */
    protected abstract HandlerResult doHandle(T request);
    
    /**
     * Handler'ın adını döndürür
     * 
     * @return Handler adı
     */
    public String getHandlerName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * Handler'ın önceliğini döndürür
     * 
     * @return Öncelik (düşük = önce çalışır)
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Handler'ın önceliğini ayarlar
     * 
     * @param priority Öncelik
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    /**
     * Handler zincirini görselleştirir
     * 
     * @return Zincir görselleştirmesi
     */
    public String visualizeChain() {
        StringBuilder sb = new StringBuilder();
        Handler<T> current = this;
        int index = 1;
        
        while (current != null) {
            sb.append(String.format("%d. %s", index++, current.getHandlerName()));
            if (current.next != null) {
                sb.append(" → ");
            }
            current = current.next;
        }
        
        return sb.toString();
    }
}

