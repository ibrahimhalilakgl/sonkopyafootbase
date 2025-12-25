package com.footbase.patterns.observer;

import com.footbase.entity.Bildirim;
import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.repository.BildirimRepository;
import com.footbase.repository.KullaniciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editör Gözlemci Sınıfı (Concrete Observer)
 * 
 * Bu sınıf, sistem editörlerini temsil eder ve
 * Observer Pattern'in somut (concrete) observer implementasyonudur.
 * 
 * Editörler, kendi ekledikleri maçlarla ilgili gelişmelerden haberdar olmak ister:
 * - Maçları onaylandığında
 * - Maçları reddedildiğinde
 * - Maçlarında değişiklik olduğunda
 * 
 * OBSERVER PATTERN'DEKİ ROLÜ:
 * Bu sınıf "Concrete Observer" rolündedir. Yöneticilerin (Admin)
 * yaptığı işlemlerden haberdar olur ve editöre bildirim gönderir.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public class EditorGozlemci implements Gozlemci {
    
    /**
     * Loglama için SLF4J logger
     */
    private static final Logger logger = LoggerFactory.getLogger(EditorGozlemci.class);
    
    /**
     * Editörün kullanıcı ID'si
     */
    private final Long editorId;
    
    /**
     * Editörün email adresi
     */
    private final String editorEmail;
    
    /**
     * Bildirim repository'si
     */
    private BildirimRepository bildirimRepository;
    
    /**
     * Kullanıcı repository'si
     */
    private KullaniciRepository kullaniciRepository;
    
    /**
     * Parametreli Constructor
     * 
     * @param editor Editör kullanıcı entity'si
     */
    public EditorGozlemci(Kullanici editor) {
        this.editorId = editor.getId();
        this.editorEmail = editor.getEmail();
    }
    
    /**
     * Parametreli Constructor
     * 
     * @param editorId Editör ID'si
     * @param editorEmail Editör email'i
     */
    public EditorGozlemci(Long editorId, String editorEmail) {
        this.editorId = editorId;
        this.editorEmail = editorEmail;
    }
    
    /**
     * Repository'leri ayarlar (Setter Injection)
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
     * @param olayTipi Olay tipi
     * @param veri Olayla ilgili veri
     */
    @Override
    public void guncelle(String olayTipi, Object veri) {
        if (veri instanceof Mac) {
            Mac mac = (Mac) veri;
            macOlayiniIsle(olayTipi, mac);
        } else {
            logger.warn("Editör Gözlemci: Bilinmeyen veri tipi: {}", 
                       veri != null ? veri.getClass().getName() : "null");
        }
    }
    
    /**
     * Maç Olaylarını İşler
     * 
     * @param olayTipi Maç olay tipi
     * @param mac İlgili maç entity'si
     */
    private void macOlayiniIsle(String olayTipi, Mac mac) {
        switch (olayTipi) {
            case "MAC_ONAYLANDI":
                macOnaylandiOlayiniIsle(mac);
                break;
            
            case "MAC_REDDEDILDI":
                macReddedildiOlayiniIsle(mac);
                break;
            
            case "MAC_GUNCELLENDI":
                macGuncellendiOlayiniIsle(mac);
                break;
            
            default:
                logger.debug("Editör Gözlemci: İlgilenilmeyen olay tipi: {}", olayTipi);
        }
    }
    
    /**
     * "Maç Onaylandı" Olayını İşler
     * 
     * Editörün eklediği maç yönetici tarafından onaylandı.
     * Editöre başarı bildirimi gönder.
     * 
     * @param mac Onaylanan maç
     */
    private void macOnaylandiOlayiniIsle(Mac mac) {
        logger.info("✅ EDITÖR BİLDİRİMİ: ID={} Email={} → Maçınız onaylandı: Mac ID={}", 
                   editorId, editorEmail, mac.getId());
        
        bildirimOlustur(
            "MAC_ONAYLANDI",
            "Maçınız Onaylandı! 🎉",
            String.format("Eklediğiniz '%s vs %s' maçı yönetici tarafından onaylandı ve yayına alındı.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
            mac,
            null // Sistem bildirimi, gönderici yok
        );
    }
    
    /**
     * "Maç Reddedildi" Olayını İşler
     * 
     * Editörün eklediği maç yönetici tarafından reddedildi.
     * Editöre red nedeni ile bildirim gönder.
     * 
     * @param mac Reddedilen maç
     */
    private void macReddedildiOlayiniIsle(Mac mac) {
        logger.info("❌ EDITÖR BİLDİRİMİ: ID={} Email={} → Maçınız reddedildi: Mac ID={}", 
                   editorId, editorEmail, mac.getId());
        
        bildirimOlustur(
            "MAC_REDDEDILDI",
            "Maçınız Reddedildi",
            String.format("Eklediğiniz '%s vs %s' maçı yönetici tarafından reddedildi. Lütfen maç bilgilerini kontrol edip tekrar ekleyin.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
            mac,
            null
        );
    }
    
    /**
     * "Maç Güncellendi" Olayını İşler
     * 
     * @param mac Güncellenen maç
     */
    private void macGuncellendiOlayiniIsle(Mac mac) {
        logger.info("📝 EDITÖR BİLDİRİMİ: ID={} → Maç güncellendi: Mac ID={}", 
                   editorId, mac.getId());
    }
    
    /**
     * Veritabanına Bildirim Kaydı Oluşturur
     * 
     * @param bildirimTipi Bildirim tipi
     * @param baslik Bildirim başlığı
     * @param icerik Bildirim içeriği
     * @param mac İlgili maç
     * @param gonderici Gönderici kullanıcı
     */
    private void bildirimOlustur(String bildirimTipi, String baslik, String icerik, 
                                 Mac mac, Kullanici gonderici) {
        if (bildirimRepository == null || kullaniciRepository == null) {
            logger.warn("Repository'ler henüz enjekte edilmedi, bildirim kaydedilemedi.");
            return;
        }
        
        try {
            Kullanici editor = kullaniciRepository.findById(editorId).orElse(null);
            if (editor == null) {
                logger.error("Editör bulunamadı: ID={}", editorId);
                return;
            }
            
            Bildirim bildirim = new Bildirim();
            bildirim.setAliciKullanici(editor);
            bildirim.setGondericiKullanici(gonderici);
            bildirim.setBildirimTipi(bildirimTipi);
            bildirim.setBaslik(baslik);
            bildirim.setIcerik(icerik);
            bildirim.setMac(mac);
            bildirim.setOkundu(false);
            
            if (mac != null && mac.getId() != null) {
                bildirim.setHedefUrl("/app/matches/" + mac.getId());
            }
            
            bildirimRepository.save(bildirim);
            
            logger.info("📬 Bildirim veritabanına kaydedildi: Alıcı ID={}, Tip={}", 
                       editorId, bildirimTipi);
            
        } catch (Exception e) {
            logger.error("Bildirim oluşturulurken hata: {}", e.getMessage(), e);
        }
    }
    
    // ==================== GETTER METODLARI ====================
    
    public Long getEditorId() {
        return editorId;
    }
    
    public String getEditorEmail() {
        return editorEmail;
    }
    
    // ==================== EQUALS VE HASHCODE ====================
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EditorGozlemci that = (EditorGozlemci) obj;
        return editorId != null && editorId.equals(that.editorId);
    }
    
    @Override
    public int hashCode() {
        return editorId != null ? editorId.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "EditorGozlemci{" +
                "editorId=" + editorId +
                ", editorEmail='" + editorEmail + '\'' +
                '}';
    }
}

