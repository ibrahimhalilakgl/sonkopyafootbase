package com.footbase.patterns.observer;

import com.footbase.entity.Bildirim;
import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.repository.BildirimRepository;
import com.footbase.repository.KullaniciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Yönetici Gözlemci Sınıfı (Concrete Observer)
 * 
 * Bu sınıf, sistem yöneticilerini (Admin) temsil eder ve
 * Observer Pattern'in somut (concrete) observer implementasyonudur.
 * 
 * Yöneticiler, sistemdeki belirli olaylardan haberdar olmak ister:
 * - Yeni maç eklendiğinde (onay için)
 * - Editörlerin yaptığı değişiklikler
 * - Sistem hataları ve uyarılar
 * 
 * Bu gözlemci, bildirimleri hem loglara yazdırır hem de
 * veritabanına kaydeder (Bildirim entity'si).
 * 
 * OBSERVER PATTERN'DEKİ ROLÜ:
 * Bu sınıf "Concrete Observer" rolündedir. Subject'ten (Konu)
 * gelen bildirimleri alır ve kendi iş mantığını uygular.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public class YoneticiGozlemci implements Gozlemci {
    
    /**
     * Loglama için SLF4J logger
     * Tüm bildirimler konsola/log dosyasına yazılır
     */
    private static final Logger logger = LoggerFactory.getLogger(YoneticiGozlemci.class);
    
    /**
     * Yöneticinin kullanıcı ID'si
     * Veritabanında bildirim oluştururken kullanılır
     */
    private final Long yoneticiId;
    
    /**
     * Yöneticinin email adresi
     * Gelecekte email gönderimi için kullanılabilir
     */
    private final String yoneticiEmail;
    
    /**
     * Bildirim repository'si
     * Bildirimleri veritabanına kaydetmek için kullanılır
     * Spring Bean olarak enjekte edilir
     */
    private BildirimRepository bildirimRepository;
    
    /**
     * Kullanıcı repository'si
     * Kullanıcı bilgilerini çekmek için kullanılır
     * Spring Bean olarak enjekte edilir
     */
    private KullaniciRepository kullaniciRepository;
    
    /**
     * Parametreli Constructor
     * 
     * Kullanıcı entity'sinden yönetici bilgilerini alır
     * 
     * @param yonetici Yönetici kullanıcı entity'si
     */
    public YoneticiGozlemci(Kullanici yonetici) {
        this.yoneticiId = yonetici.getId();
        this.yoneticiEmail = yonetici.getEmail();
    }
    
    /**
     * Parametreli Constructor
     * 
     * ID ve email ile doğrudan yönetici oluşturur
     * 
     * @param yoneticiId Yönetici ID'si
     * @param yoneticiEmail Yönetici email'i
     */
    public YoneticiGozlemci(Long yoneticiId, String yoneticiEmail) {
        this.yoneticiId = yoneticiId;
        this.yoneticiEmail = yoneticiEmail;
    }
    
    /**
     * Repository'leri ayarlar
     * 
     * Spring Bean injection ile repository'ler set edilir.
     * Constructor injection kullanamadığımız için (Observer'lar
     * dinamik oluşturuluyor) setter injection kullanıyoruz.
     * 
     * @param bildirimRepository Bildirim repository
     * @param kullaniciRepository Kullanıcı repository
     */
    public void setRepositories(BildirimRepository bildirimRepository, 
                                KullaniciRepository kullaniciRepository) {
        this.bildirimRepository = bildirimRepository;
        this.kullaniciRepository = kullaniciRepository;
    }
    
    /**
     * Güncelleme Metodunu İşler (Observer Pattern)
     * 
     * Bu metod, Subject (Konu) tarafından çağrılır.
     * Gelen olay tipine göre uygun işlemi yapar.
     * 
     * @param olayTipi Olay tipi ("MAC_EKLENDI", "MAC_ONAYLANDI" vb.)
     * @param veri Olayla ilgili veri (genellikle Mac entity'si)
     */
    @Override
    public void guncelle(String olayTipi, Object veri) {
        // Veri tipini kontrol et (Tip güvenliği)
        if (veri instanceof Mac) {
            Mac mac = (Mac) veri;
            macOlayiniIsle(olayTipi, mac);
        } else {
            logger.warn("Yönetici Gözlemci: Bilinmeyen veri tipi geldi: {}", 
                       veri != null ? veri.getClass().getName() : "null");
        }
    }
    
    /**
     * Maç Olaylarını İşler
     * 
     * Farklı maç olayları için farklı aksiyonlar alır:
     * - MAC_EKLENDI: Yeni maç onay bekliyor
     * - MAC_ONAYLANDI: Maç başarıyla onaylandı
     * - MAC_REDDEDILDI: Maç reddedildi
     * 
     * Her olay için hem log kaydı hem de veritabanı bildirimi oluşturulur.
     * 
     * @param olayTipi Maç olay tipi
     * @param mac İlgili maç entity'si
     */
    private void macOlayiniIsle(String olayTipi, Mac mac) {
        switch (olayTipi) {
            case "MAC_EKLENDI":
                macEklendiOlayiniIsle(mac);
                break;
            
            case "MAC_ONAYLANDI":
                macOnaylandiOlayiniIsle(mac);
                break;
            
            case "MAC_REDDEDILDI":
                macReddedildiOlayiniIsle(mac);
                break;
            
            case "MAC_BASLADI":
                macBasladiOlayiniIsle(mac);
                break;
            
            case "MAC_BITTI":
                macBittiOlayiniIsle(mac);
                break;
            
            default:
                logger.warn("Yönetici Gözlemci: Bilinmeyen olay tipi: {}", olayTipi);
        }
    }
    
    /**
     * "Maç Eklendi" Olayını İşler
     * 
     * Bir editör yeni maç eklediğinde, yöneticiye bildirim gönderilir.
     * Yönetici bu maçı onaylamalı veya reddetmelidir.
     * 
     * @param mac Eklenen maç
     */
    private void macEklendiOlayiniIsle(Mac mac) {
        logger.info("✉️ YÖNETİCİ BİLDİRİMİ: ID={} Email={} → Yeni maç onay bekliyor: Mac ID={}", 
                   yoneticiId, yoneticiEmail, mac.getId());
        
        // Veritabanına bildirim kaydı oluştur
        bildirimOlustur(
            "MAC_EKLENDI",
            "Yeni Maç Onay Bekliyor",
            String.format("%s vs %s maçı eklendi ve onayınızı bekliyor.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
            mac,
            mac.getEditor()
        );
    }
    
    /**
     * "Maç Onaylandı" Olayını İşler
     * 
     * Bir yönetici maçı onayladığında, diğer yöneticilere bilgi verilir.
     * 
     * @param mac Onaylanan maç
     */
    private void macOnaylandiOlayiniIsle(Mac mac) {
        logger.info("✅ YÖNETİCİ BİLDİRİMİ: ID={} Email={} → Maç onaylandı: Mac ID={}", 
                   yoneticiId, yoneticiEmail, mac.getId());
        
        bildirimOlustur(
            "MAC_ONAYLANDI",
            "Maç Onaylandı",
            String.format("%s vs %s maçı başarıyla onaylandı ve yayına alındı.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
            mac,
            null
        );
    }
    
    /**
     * "Maç Reddedildi" Olayını İşler
     * 
     * Bir yönetici maçı reddettiyse, diğer yöneticilere bilgi verilir.
     * 
     * @param mac Reddedilen maç
     */
    private void macReddedildiOlayiniIsle(Mac mac) {
        logger.info("❌ YÖNETİCİ BİLDİRİMİ: ID={} Email={} → Maç reddedildi: Mac ID={}", 
                   yoneticiId, yoneticiEmail, mac.getId());
        
        bildirimOlustur(
            "MAC_REDDEDILDI",
            "Maç Reddedildi",
            String.format("%s vs %s maçı reddedildi.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
            mac,
            null
        );
    }
    
    /**
     * "Maç Başladı" Olayını İşler
     * 
     * @param mac Başlayan maç
     */
    private void macBasladiOlayiniIsle(Mac mac) {
        logger.info("⚽ YÖNETİCİ BİLDİRİMİ: ID={} → Maç başladı: Mac ID={}", 
                   yoneticiId, mac.getId());
    }
    
    /**
     * "Maç Bitti" Olayını İşler
     * 
     * @param mac Biten maç
     */
    private void macBittiOlayiniIsle(Mac mac) {
        logger.info("🏁 YÖNETİCİ BİLDİRİMİ: ID={} → Maç bitti: Mac ID={}", 
                   yoneticiId, mac.getId());
    }
    
    /**
     * Veritabanına Bildirim Kaydı Oluşturur
     * 
     * Bu metod, bildirim entity'si oluşturup veritabanına kaydeder.
     * Repository null kontrolü yapar (başlatma sırasında null olabilir).
     * 
     * @param bildirimTipi Bildirim tipi
     * @param baslik Bildirim başlığı
     * @param icerik Bildirim içeriği
     * @param mac İlgili maç (opsiyonel)
     * @param gonderici Bildirimi tetikleyen kullanıcı (opsiyonel)
     */
    private void bildirimOlustur(String bildirimTipi, String baslik, String icerik, 
                                 Mac mac, Kullanici gonderici) {
        // Repository kontrolü (null ise veritabanına kaydetme)
        if (bildirimRepository == null || kullaniciRepository == null) {
            logger.warn("Repository'ler henüz enjekte edilmedi, bildirim kaydedilemedi.");
            return;
        }
        
        try {
            // Yönetici kullanıcıyı veritabanından çek
            Kullanici yonetici = kullaniciRepository.findById(yoneticiId).orElse(null);
            if (yonetici == null) {
                logger.error("Yönetici bulunamadı: ID={}", yoneticiId);
                return;
            }
            
            // Bildirim entity'si oluştur
            Bildirim bildirim = new Bildirim();
            bildirim.setAliciKullanici(yonetici);
            bildirim.setGondericiKullanici(gonderici);
            bildirim.setBildirimTipi(bildirimTipi);
            bildirim.setBaslik(baslik);
            bildirim.setIcerik(icerik);
            bildirim.setMac(mac);
            bildirim.setOkundu(false);
            
            // Hedef URL oluştur (maç detay sayfası)
            if (mac != null && mac.getId() != null) {
                bildirim.setHedefUrl("/app/matches/" + mac.getId());
            }
            
            // Veritabanına kaydet
            bildirimRepository.save(bildirim);
            
            logger.info("📬 Bildirim veritabanına kaydedildi: Alıcı ID={}, Tip={}", 
                       yoneticiId, bildirimTipi);
            
        } catch (Exception e) {
            logger.error("Bildirim oluşturulurken hata: {}", e.getMessage(), e);
        }
    }
    
    // ==================== GETTER METODLARI ====================
    
    /**
     * Yönetici ID'sini döndürür
     * @return Yönetici ID
     */
    public Long getYoneticiId() {
        return yoneticiId;
    }
    
    /**
     * Yönetici email'ini döndürür
     * @return Yönetici email
     */
    public String getYoneticiEmail() {
        return yoneticiEmail;
    }
    
    // ==================== EQUALS VE HASHCODE ====================
    
    /**
     * İki yönetici gözlemcinin eşit olup olmadığını kontrol eder
     * Sadece ID'ye göre karşılaştırma yapar
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        YoneticiGozlemci that = (YoneticiGozlemci) obj;
        return yoneticiId != null && yoneticiId.equals(that.yoneticiId);
    }
    
    /**
     * Hash code hesaplar
     * Sadece ID'ye göre hesaplama yapar
     */
    @Override
    public int hashCode() {
        return yoneticiId != null ? yoneticiId.hashCode() : 0;
    }
    
    /**
     * String temsili
     */
    @Override
    public String toString() {
        return "YoneticiGozlemci{" +
                "yoneticiId=" + yoneticiId +
                ", yoneticiEmail='" + yoneticiEmail + '\'' +
                '}';
    }
}

