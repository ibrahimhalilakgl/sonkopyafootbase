package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Değerlendirme Stratejisi Factory
 * 
 * Strategy Pattern ile Factory Pattern kombinasyonu.
 * Kullanıcı rolüne göre doğru strateji nesnesini döndürür.
 * 
 * Bu sınıf Strategy + Factory Pattern birleşimi örneğidir.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class DegerlendirmeStratejiFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DegerlendirmeStratejiFactory.class);
    
    @Autowired
    private AdminDegerlendirmeStrateji adminStrateji;
    
    @Autowired
    private EditorDegerlendirmeStrateji editorStrateji;
    
    @Autowired
    private NormalKullaniciDegerlendirmeStrateji normalKullaniciStrateji;
    
    public DegerlendirmeStratejiFactory() {
        logger.info("🏭 DegerlendirmeStratejiFactory oluşturuldu (Strategy + Factory Pattern)");
    }
    
    /**
     * Kullanıcı rolüne göre strateji oluşturur
     * 
     * @param rol Kullanıcı rolü ("ADMIN", "EDITOR", "USER")
     * @return İlgili strateji nesnesi
     * @throws IllegalArgumentException Geçersiz rol için
     */
    public DegerlendirmeStrateji getStrateji(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            logger.error("❌ Rol boş olamaz!");
            throw new IllegalArgumentException("Rol boş olamaz!");
        }
        
        String normalizedRol = rol.toUpperCase().trim();
        
        DegerlendirmeStrateji strateji = switch (normalizedRol) {
            case "ADMIN", "YONETICI" -> {
                logger.debug("🏭 Admin stratejisi döndürülüyor");
                yield adminStrateji;
            }
            case "EDITOR", "EDITÖR" -> {
                logger.debug("🏭 Editör stratejisi döndürülüyor");
                yield editorStrateji;
            }
            case "USER", "KULLANICI", "NORMAL" -> {
                logger.debug("🏭 Normal kullanıcı stratejisi döndürülüyor");
                yield normalKullaniciStrateji;
            }
            default -> {
                logger.error("❌ Bilinmeyen rol: {}", rol);
                throw new IllegalArgumentException("Geçersiz rol: " + rol);
            }
        };
        
        logger.info("✅ Strateji seçildi: {} (Ağırlık: {}x)", 
                   strateji.getStratejAdi(), strateji.getAgirlik());
        
        return strateji;
    }
    
    /**
     * Tüm stratejileri listeler
     * 
     * @return Strateji listesi
     */
    public java.util.List<DegerlendirmeStrateji> tumStratejiler() {
        return java.util.Arrays.asList(
            adminStrateji,
            editorStrateji,
            normalKullaniciStrateji
        );
    }
    
    /**
     * Kullanılabilir rolleri listeler
     * 
     * @return Rol listesi
     */
    public java.util.List<String> kullanilabilirRoller() {
        return java.util.Arrays.asList("ADMIN", "EDITOR", "USER");
    }
    
    /**
     * Rol geçerli mi kontrol eder
     * 
     * @param rol Kontrol edilecek rol
     * @return Geçerli ise true
     */
    public boolean isValidRol(String rol) {
        if (rol == null) return false;
        
        String normalizedRol = rol.toUpperCase().trim();
        
        return normalizedRol.equals("ADMIN") || normalizedRol.equals("YONETICI") ||
               normalizedRol.equals("EDITOR") || normalizedRol.equals("EDITÖR") ||
               normalizedRol.equals("USER") || normalizedRol.equals("KULLANICI") ||
               normalizedRol.equals("NORMAL");
    }
}

