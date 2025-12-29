package com.footbase.patterns.command;

/**
 * Command Pattern - Command Interface
 * GoF Design Pattern
 * 
 * Amaç: İşlemleri nesneler olarak kapsüllemek
 * - Execute: İşlemi çalıştır
 * - Undo: İşlemi geri al
 * - Redo: İşlemi tekrar yap
 * - getDescription: İşlem açıklaması
 */
public interface Command {
    
    /**
     * Komutu çalıştırır
     * @return İşlem başarılıysa true
     */
    boolean execute();
    
    /**
     * Komutu geri alır (undo)
     * @return Geri alma başarılıysa true
     */
    boolean undo();
    
    /**
     * Komutu tekrar yapar (redo)
     * @return Tekrar başarılıysa true
     */
    boolean redo();
    
    /**
     * Komut açıklaması
     * @return İşlem açıklaması
     */
    String getDescription();
    
    /**
     * Komut tipi
     * @return Komut tipi (SKOR_GIRISI, MAC_SONLANDIR, vb.)
     */
    String getCommandType();
    
    /**
     * İşlem yapan kullanıcı ID
     * @return Kullanıcı ID
     */
    Long getKullaniciId();
    
    /**
     * İşlem zamanı
     * @return Timestamp
     */
    java.time.LocalDateTime getExecutionTime();
}


