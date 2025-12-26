package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Değerlendirme Context (Strategy Pattern Context)
 * 
 * Stratejileri kullanan ve yöneten sınıf.
 * Kullanıcı tipine göre doğru stratejiyi seçer ve kullanır.
 * 
 * KULLANIM:
 * 1. Kullanıcı rolüne göre strateji seç
 * 2. Strateji ile puanı hesapla
 * 3. Toplam değerlendirmeyi hesapla
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class DegerlendirmeContext {
    
    private static final Logger logger = LoggerFactory.getLogger(DegerlendirmeContext.class);
    
    @Autowired
    private AdminDegerlendirmeStrateji adminStrateji;
    
    @Autowired
    private EditorDegerlendirmeStrateji editorStrateji;
    
    @Autowired
    private NormalKullaniciDegerlendirmeStrateji normalKullaniciStrateji;
    
    private DegerlendirmeStrateji aktifStrateji;
    
    public DegerlendirmeContext() {
        logger.info("🎯 DegerlendirmeContext oluşturuldu (Strategy Pattern)");
    }
    
    /**
     * Kullanıcı rolüne göre strateji seçer
     * 
     * @param rol Kullanıcı rolü ("ADMIN", "EDITOR", "USER")
     */
    public void stratejiSec(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            logger.error("❌ Rol boş olamaz!");
            throw new IllegalArgumentException("Rol boş olamaz!");
        }
        
        String normalizedRol = rol.toUpperCase().trim();
        
        aktifStrateji = switch (normalizedRol) {
            case "ADMIN", "YONETICI" -> {
                logger.info("👑 Admin stratejisi seçildi (3x ağırlık)");
                yield adminStrateji;
            }
            case "EDITOR", "EDITÖR" -> {
                logger.info("✏️ Editör stratejisi seçildi (2x ağırlık)");
                yield editorStrateji;
            }
            case "USER", "KULLANICI", "NORMAL" -> {
                logger.info("👤 Normal kullanıcı stratejisi seçildi (1x ağırlık)");
                yield normalKullaniciStrateji;
            }
            default -> {
                logger.error("❌ Bilinmeyen rol: {}", rol);
                throw new IllegalArgumentException("Geçersiz rol: " + rol);
            }
        };
        
        logger.debug("✓ Strateji değiştirildi: {}", aktifStrateji.getStratejAdi());
    }
    
    /**
     * Aktif strateji ile puanı hesaplar
     * 
     * @param yildizSayisi Verilen yıldız sayısı (1-5)
     * @return Ağırlıklandırılmış puan
     */
    public double puanHesapla(int yildizSayisi) {
        if (aktifStrateji == null) {
            logger.error("❌ Strateji seçilmedi! Önce stratejiSec() çağırın.");
            throw new IllegalStateException("Strateji seçilmedi!");
        }
        
        return aktifStrateji.puanHesapla(yildizSayisi);
    }
    
    /**
     * Kullanıcı rolü ve yıldız sayısı ile direkt hesaplama yapar
     * 
     * @param rol Kullanıcı rolü
     * @param yildizSayisi Verilen yıldız sayısı
     * @return Ağırlıklandırılmış puan
     */
    public double hesapla(String rol, int yildizSayisi) {
        stratejiSec(rol);
        return puanHesapla(yildizSayisi);
    }
    
    /**
     * Aktif stratejinin bilgilerini döndürür
     * 
     * @return Strateji bilgisi
     */
    public String getAktifStrateji() {
        if (aktifStrateji == null) {
            return "Strateji seçilmedi";
        }
        return String.format("%s (Ağırlık: %.1fx)", 
                           aktifStrateji.getStratejAdi(), 
                           aktifStrateji.getAgirlik());
    }
    
    /**
     * Aktif stratejinin ağırlığını döndürür
     * 
     * @return Ağırlık katsayısı
     */
    public double getAgirlik() {
        if (aktifStrateji == null) {
            throw new IllegalStateException("Strateji seçilmedi!");
        }
        return aktifStrateji.getAgirlik();
    }
    
    /**
     * Birden fazla değerlendirmenin toplam puanını hesaplar
     * 
     * @param degerlendirmeler Değerlendirme listesi (rol, yıldız çiftleri)
     * @return Toplam ağırlıklandırılmış puan
     */
    public double toplamPuanHesapla(java.util.List<Degerlendirme> degerlendirmeler) {
        logger.info("📊 Toplam {} değerlendirme hesaplanıyor...", degerlendirmeler.size());
        
        double toplam = 0.0;
        int adminSayisi = 0;
        int editorSayisi = 0;
        int normalSayisi = 0;
        
        for (Degerlendirme deg : degerlendirmeler) {
            double puan = hesapla(deg.getRol(), deg.getYildizSayisi());
            toplam += puan;
            
            // İstatistik
            String rol = deg.getRol().toUpperCase();
            if (rol.equals("ADMIN") || rol.equals("YONETICI")) {
                adminSayisi++;
            } else if (rol.equals("EDITOR") || rol.equals("EDITÖR")) {
                editorSayisi++;
            } else {
                normalSayisi++;
            }
        }
        
        logger.info("📈 Değerlendirme İstatistikleri:");
        logger.info("   👑 Admin: {} değerlendirme (3x ağırlık)", adminSayisi);
        logger.info("   ✏️ Editör: {} değerlendirme (2x ağırlık)", editorSayisi);
        logger.info("   👤 Normal: {} değerlendirme (1x ağırlık)", normalSayisi);
        logger.info("   💯 Toplam Puan: {}", toplam);
        
        return toplam;
    }
    
    /**
     * Ortalama puanı hesaplar (normalize edilmiş, 0-5 arası)
     * 
     * @param degerlendirmeler Değerlendirme listesi
     * @return Ortalama puan (0-5 arası)
     */
    public double ortalamaPuanHesapla(java.util.List<Degerlendirme> degerlendirmeler) {
        if (degerlendirmeler == null || degerlendirmeler.isEmpty()) {
            logger.warn("⚠️ Değerlendirme listesi boş!");
            return 0.0;
        }
        
        double toplamPuan = toplamPuanHesapla(degerlendirmeler);
        
        // Ağırlıkları topla
        double toplamAgirlik = 0.0;
        for (Degerlendirme deg : degerlendirmeler) {
            stratejiSec(deg.getRol());
            toplamAgirlik += getAgirlik();
        }
        
        // Normalize et (0-5 arası)
        double ortalama = (toplamPuan / toplamAgirlik);
        
        logger.info("⭐ Ortalama Puan: {}/5.0", String.format("%.2f", ortalama));
        
        return ortalama;
    }
    
    /**
     * Değerlendirme sınıfı (iç sınıf)
     */
    public static class Degerlendirme {
        private String rol;
        private int yildizSayisi;
        
        public Degerlendirme(String rol, int yildizSayisi) {
            this.rol = rol;
            this.yildizSayisi = yildizSayisi;
        }
        
        public String getRol() {
            return rol;
        }
        
        public int getYildizSayisi() {
            return yildizSayisi;
        }
    }
}

