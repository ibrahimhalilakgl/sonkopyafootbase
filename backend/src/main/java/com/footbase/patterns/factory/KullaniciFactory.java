package com.footbase.patterns.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Kullanıcı Factory (Creator - Factory Method Pattern)
 * 
 * Bu sınıf, Factory Design Pattern'in Creator rolünü üstlenir.
 * Farklı kullanıcı tiplerini oluşturmak için merkezi bir nokta sağlar.
 * 
 * FACTORY PATTERN'DEKİ ROLÜ:
 * Creator - Product nesnelerini üreten factory sınıfı
 * 
 * AVANTAJLARI:
 * 1. Nesne oluşturma mantığını merkezi hale getirir
 * 2. Kod tekrarını önler
 * 3. Yeni kullanıcı tipi eklemek kolay
 * 4. Dependency Injection ile uyumlu
 * 5. Test edilebilir
 * 
 * KULLANIM:
 * ```java
 * Kullanici admin = KullaniciFactory.createKullanici("ADMIN", "Ahmet Admin");
 * admin.login();
 * if (admin.hasPermission("MATCH_APPROVE")) {
 *     // Maç onaylama işlemi
 * }
 * ```
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class KullaniciFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(KullaniciFactory.class);
    
    /**
     * Rol bilgisine göre kullanıcı nesnesi oluşturur (basit versiyon)
     * 
     * @param rol Kullanıcı rolü (ADMIN, EDITOR, USER)
     * @return Oluşturulan kullanıcı nesnesi
     * @throws IllegalArgumentException Geçersiz rol için
     */
    public static Kullanici createKullanici(String rol) {
        logger.info("🏭 Factory: Kullanıcı oluşturuluyor - Rol: {}", rol);
        
        if (rol == null || rol.trim().isEmpty()) {
            logger.error("❌ Geçersiz rol: null veya boş");
            throw new IllegalArgumentException("Rol boş olamaz!");
        }
        
        return switch (rol.toUpperCase()) {
            case "ADMIN", "YONETICI" -> {
                logger.info("✅ AdminKullanici oluşturuldu");
                yield new AdminKullanici();
            }
            case "EDITOR", "EDITÖR" -> {
                logger.info("✅ EditorKullanici oluşturuldu");
                yield new EditorKullanici();
            }
            case "USER", "KULLANICI", "NORMAL" -> {
                logger.info("✅ NormalKullanici oluşturuldu");
                yield new NormalKullanici();
            }
            default -> {
                logger.error("❌ Bilinmeyen rol: {}", rol);
                throw new IllegalArgumentException("Geçersiz rol: " + rol);
            }
        };
    }
    
    /**
     * Rol ve görünen ad ile kullanıcı nesnesi oluşturur (gelişmiş versiyon)
     * 
     * @param rol Kullanıcı rolü (ADMIN, EDITOR, USER)
     * @param displayName Görünen ad
     * @return Oluşturulan kullanıcı nesnesi
     * @throws IllegalArgumentException Geçersiz rol için
     */
    public static Kullanici createKullanici(String rol, String displayName) {
        logger.info("🏭 Factory: Kullanıcı oluşturuluyor - Rol: {}, İsim: {}", rol, displayName);
        
        if (rol == null || rol.trim().isEmpty()) {
            logger.error("❌ Geçersiz rol: null veya boş");
            throw new IllegalArgumentException("Rol boş olamaz!");
        }
        
        return switch (rol.toUpperCase()) {
            case "ADMIN", "YONETICI" -> {
                logger.info("✅ AdminKullanici oluşturuldu: {}", displayName);
                yield new AdminKullanici(displayName);
            }
            case "EDITOR", "EDITÖR" -> {
                logger.info("✅ EditorKullanici oluşturuldu: {}", displayName);
                yield new EditorKullanici(displayName);
            }
            case "USER", "KULLANICI", "NORMAL" -> {
                logger.info("✅ NormalKullanici oluşturuldu: {}", displayName);
                yield new NormalKullanici(displayName);
            }
            default -> {
                logger.error("❌ Bilinmeyen rol: {}", rol);
                throw new IllegalArgumentException("Geçersiz rol: " + rol);
            }
        };
    }
    
    /**
     * com.footbase.entity.Kullanici entity'sinden Factory pattern kullanici oluşturur
     * 
     * @param kullaniciEntity Veritabanından gelen kullanıcı entity
     * @return Factory pattern kullanıcı nesnesi
     */
    public static Kullanici fromEntity(com.footbase.entity.Kullanici kullaniciEntity) {
        if (kullaniciEntity == null) {
            throw new IllegalArgumentException("Kullanıcı entity null olamaz!");
        }
        
        String displayName = kullaniciEntity.getEmail(); // Veya ad-soyad
        String rol = kullaniciEntity.getRol() != null ? kullaniciEntity.getRol() : "USER";
        
        logger.info("🔄 Entity'den Factory kullanıcı oluşturuluyor: {} ({})", displayName, rol);
        
        return createKullanici(rol, displayName);
    }
    
    /**
     * Kullanılabilir rolleri döndürür
     * 
     * @return Rol listesi
     */
    public static java.util.List<String> getAvailableRoles() {
        return java.util.Arrays.asList("ADMIN", "EDITOR", "USER");
    }
    
    /**
     * Rolün geçerli olup olmadığını kontrol eder
     * 
     * @param rol Kontrol edilecek rol
     * @return Geçerliyse true
     */
    public static boolean isValidRole(String rol) {
        if (rol == null) return false;
        return getAvailableRoles().contains(rol.toUpperCase()) ||
               rol.equalsIgnoreCase("YONETICI") ||
               rol.equalsIgnoreCase("EDITÖR") ||
               rol.equalsIgnoreCase("KULLANICI");
    }
}

