package com.footbase.patterns.chain;

/**
 * Handler Sonucu (Chain of Responsibility Pattern)
 * 
 * Handler işleminin sonucunu temsil eder.
 * Başarı durumu ve mesaj içerir.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public class HandlerResult {
    
    private final boolean success;
    private final String message;
    private final String handlerName;
    
    private HandlerResult(boolean success, String message, String handlerName) {
        this.success = success;
        this.message = message;
        this.handlerName = handlerName;
    }
    
    /**
     * Başarılı sonuç oluşturur
     * 
     * @param message Başarı mesajı
     * @return Başarılı sonuç
     */
    public static HandlerResult success(String message) {
        return new HandlerResult(true, message, null);
    }
    
    /**
     * Başarılı sonuç oluşturur
     * 
     * @return Başarılı sonuç
     */
    public static HandlerResult success() {
        return new HandlerResult(true, "İşlem başarılı", null);
    }
    
    /**
     * Başarısız sonuç oluşturur
     * 
     * @param message Hata mesajı
     * @return Başarısız sonuç
     */
    public static HandlerResult failure(String message) {
        return new HandlerResult(false, message, null);
    }
    
    /**
     * Başarısız sonuç oluşturur (handler adı ile)
     * 
     * @param message Hata mesajı
     * @param handlerName Handler adı
     * @return Başarısız sonuç
     */
    public static HandlerResult failure(String message, String handlerName) {
        return new HandlerResult(false, message, handlerName);
    }
    
    /**
     * İşlem başarılı mı?
     * 
     * @return Başarılı ise true
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Sonuç mesajını döndürür
     * 
     * @return Mesaj
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Handler adını döndürür
     * 
     * @return Handler adı
     */
    public String getHandlerName() {
        return handlerName;
    }
    
    @Override
    public String toString() {
        return String.format("HandlerResult{success=%s, message='%s', handler='%s'}", 
                           success, message, handlerName);
    }
}

