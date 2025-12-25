package com.footbase.patterns.factory;

import java.util.List;

/**
 * Kullanıcı Interface (Product - Factory Pattern)
 * 
 * Bu interface, Factory Design Pattern'in Product rolünü üstlenir.
 * Farklı kullanıcı tiplerinin ortak davranışlarını tanımlar.
 * 
 * FACTORY PATTERN'DEKİ ROLÜ:
 * Product - Factory tarafından üretilen nesnelerin arayüzü
 * 
 * AVANTAJLARI:
 * 1. Kod tekrarını önler
 * 2. Yeni kullanıcı tipi eklemek kolay
 * 3. Tek sorumluluk prensibi (Single Responsibility)
 * 4. Açık/Kapalı prensibi (Open/Closed)
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public interface Kullanici {
    
    /**
     * Kullanıcının giriş yapması
     * Her kullanıcı tipi kendi giriş mantığını uygular
     */
    void login();
    
    /**
     * Kullanıcının yetkilerini döndürür
     * @return Yetki listesi (örn: ["MATCH_CREATE", "MATCH_EDIT"])
     */
    List<String> getPermissions();
    
    /**
     * Kullanıcının rolünü döndürür
     * @return Rol adı (ADMIN, EDITOR, USER)
     */
    String getRole();
    
    /**
     * Kullanıcının görünen adını döndürür
     * @return Kullanıcı adı
     */
    String getDisplayName();
    
    /**
     * Belirli bir işlem için yetki kontrolü yapar
     * @param permission Kontrol edilecek yetki
     * @return Yetkisi varsa true, yoksa false
     */
    default boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }
}

