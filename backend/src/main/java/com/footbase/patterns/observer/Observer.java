package com.footbase.patterns.observer;

/**
 * Observer interface (Observer Pattern)
 * Subject'teki değişikliklerden haberdar olmak isteyen sınıflar için arayüz
 */
public interface Observer {
    
    /**
     * Subject'ten gelen bildirimi işler
     * @param eventType Olay tipi (ör: "MAC_EKLENDI", "MAC_ONAYLANDI")
     * @param data Olayla ilgili veri
     */
    void update(String eventType, Object data);
}
