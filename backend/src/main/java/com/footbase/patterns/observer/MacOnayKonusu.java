package com.footbase.patterns.observer;

import com.footbase.entity.Mac;
import com.footbase.repository.BildirimRepository;
import com.footbase.repository.KullaniciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Maç Onay Konusu (Concrete Subject - Observer Pattern)
 * 
 * Bu sınıf, Observer Design Pattern'in Subject (Konu) rolünü üstlenir.
 * Maç onay süreçlerini yönetir ve ilgili gözlemcilere bildirim gönderir.
 * 
 * OBSERVER PATTERN'DEKİ ROLÜ:
 * Subject (Konu) - Gözlemcileri yönetir ve değişiklikleri bildirir
 * 
 * ÇALIŞMA AKIŞI:
 * 1. Yöneticiler ve editörler kendilerini gözlemci olarak kaydeder
 * 2. Maç ile ilgili bir olay gerçekleşir (ekleme, onay, red)
 * 3. Bu sınıf, ilgili tüm gözlemcilere bildirim gönderir
 * 4. Her gözlemci bildirimi alır ve kendi işlemini yapar
 * 
 * KULLANIM ÖRNEĞİ:
 * ```java
 * // Yönetici gözlemci oluştur ve kaydet
 * MacOnayKonusu konu = new MacOnayKonusu();
 * YoneticiGozlemci admin = new YoneticiGozlemci(adminKullanici);
 * konu.ekle(admin);
 * 
 * // Maç eklendi, gözlemcilere bildir
 * konu.macEklendi(yeniMac);
 * ```
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class MacOnayKonusu implements Konu {
    
    /**
     * Loglama için logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MacOnayKonusu.class);
    
    /**
     * Kayıtlı gözlemcilerin listesi
     * Thread-safe koleksiyon kullanılabilir (gelecek iyileştirme)
     */
    private final List<Gozlemci> gozlemciler = new ArrayList<>();
    
    /**
     * Şu anki maç (en son işlenen)
     */
    private Mac aktifMac;
    
    /**
     * Şu anki olay tipi
     */
    private String aktifOlayTipi;
    
    /**
     * Bildirim repository (Spring tarafından otomatik enjekte edilir)
     */
    @Autowired(required = false) // required=false çünkü test ortamında olmayabilir
    private BildirimRepository bildirimRepository;
    
    /**
     * Kullanıcı repository (Spring tarafından otomatik enjekte edilir)
     */
    @Autowired(required = false)
    private KullaniciRepository kullaniciRepository;
    
    /**
     * Varsayılan constructor
     */
    public MacOnayKonusu() {
        logger.info("🔔 MacOnayKonusu oluşturuldu (Observer Pattern Subject)");
    }
    
    /**
     * Bir gözlemciyi kayıt listesine ekler
     * 
     * Aynı gözlemci birden fazla kez eklenemez (equals kontrolü yapılır).
     * Eklenen gözlemciye repository'ler enjekte edilir.
     * 
     * @param gozlemci Eklenecek gözlemci
     */
    @Override
    public void ekle(Gozlemci gozlemci) {
        if (gozlemci == null) {
            logger.warn("Null gözlemci eklenemez!");
            return;
        }
        
        if (!gozlemciler.contains(gozlemci)) {
            gozlemciler.add(gozlemci);
            
            // Repository'leri gözlemciye enjekte et
            // Gözlemci, YoneticiGozlemci veya EditorGozlemci olabilir
            if (gozlemci instanceof YoneticiGozlemci) {
                YoneticiGozlemci yonetici = (YoneticiGozlemci) gozlemci;
                yonetici.setRepositories(bildirimRepository, kullaniciRepository);
                logger.info("👨‍💼 Yönetici gözlemci eklendi: ID={}", yonetici.getYoneticiId());
            } else if (gozlemci instanceof EditorGozlemci) {
                EditorGozlemci editor = (EditorGozlemci) gozlemci;
                editor.setRepositories(bildirimRepository, kullaniciRepository);
                logger.info("✍️ Editör gözlemci eklendi: ID={}", editor.getEditorId());
            } else {
                logger.info("➕ Gözlemci eklendi: {}", gozlemci.getClass().getSimpleName());
            }
        } else {
            logger.debug("Gözlemci zaten kayıtlı, tekrar eklenmedi.");
        }
    }
    
    /**
     * Bir gözlemciyi kayıt listesinden çıkarır
     * 
     * Gözlemci artık bildirimleri almayacaktır.
     * 
     * @param gozlemci Çıkarılacak gözlemci
     */
    @Override
    public void cikar(Gozlemci gozlemci) {
        if (gozlemci == null) {
            logger.warn("Null gözlemci çıkarılamaz!");
            return;
        }
        
        boolean cikarildi = gozlemciler.remove(gozlemci);
        if (cikarildi) {
            if (gozlemci instanceof YoneticiGozlemci) {
                YoneticiGozlemci yonetici = (YoneticiGozlemci) gozlemci;
                logger.info("👨‍💼 Yönetici gözlemci çıkarıldı: ID={}", yonetici.getYoneticiId());
            } else if (gozlemci instanceof EditorGozlemci) {
                EditorGozlemci editor = (EditorGozlemci) gozlemci;
                logger.info("✍️ Editör gözlemci çıkarıldı: ID={}", editor.getEditorId());
            } else {
                logger.info("➖ Gözlemci çıkarıldı: {}", gozlemci.getClass().getSimpleName());
            }
        } else {
            logger.debug("Gözlemci listede bulunamadı, çıkarılamadı.");
        }
    }
    
    /**
     * Kayıtlı tüm gözlemcilere bildirim gönderir
     * 
     * Her gözlemcinin guncelle() metodu sırayla çağrılır.
     * Eğer bir gözlemcide hata oluşursa, diğer gözlemciler etkilenmez.
     */
    @Override
    public void gozlemcileriBilgilendir() {
        if (gozlemciler.isEmpty()) {
            logger.warn("⚠️ Hiç gözlemci kayıtlı değil, bildirim gönderilmedi!");
            return;
        }
        
        logger.info("📢 {} gözlemciye bildirim gönderiliyor: Olay={}, Mac ID={}", 
                   gozlemciler.size(), aktifOlayTipi, 
                   aktifMac != null ? aktifMac.getId() : "null");
        
        // Her gözlemciye bildirim gönder
        int basariliGonderim = 0;
        for (Gozlemci gozlemci : gozlemciler) {
            try {
                gozlemci.guncelle(aktifOlayTipi, aktifMac);
                basariliGonderim++;
            } catch (Exception e) {
                logger.error("Gözlemci bilgilendirilirken hata: {}", e.getMessage(), e);
                // Diğer gözlemcilere devam et
            }
        }
        
        logger.info("✅ Bildirim tamamlandı: {}/{} gözlemciye başarıyla ulaştı", 
                   basariliGonderim, gozlemciler.size());
    }
    
    /**
     * Yeni maç eklendiğinde çağrılır
     * 
     * Tüm yöneticilere "yeni maç onay bekliyor" bildirimi gönderilir.
     * 
     * @param mac Eklenen maç
     */
    public void macEklendi(Mac mac) {
        if (mac == null) {
            logger.error("Null maç ile macEklendi çağrıldı!");
            return;
        }
        
        logger.info("🆕 Yeni maç eklendi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_EKLENDI";
        gozlemcileriBilgilendir();
    }
    
    /**
     * Maç onaylandığında çağrılır
     * 
     * İlgili editöre "maç onaylandı" bildirimi gönderilir.
     * 
     * @param mac Onaylanan maç
     */
    public void macOnaylandi(Mac mac) {
        if (mac == null) {
            logger.error("Null maç ile macOnaylandi çağrıldı!");
            return;
        }
        
        logger.info("✅ Maç onaylandı: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_ONAYLANDI";
        gozlemcileriBilgilendir();
    }
    
    /**
     * Maç reddedildiğinde çağrılır
     * 
     * İlgili editöre "maç reddedildi" bildirimi gönderilir.
     * 
     * @param mac Reddedilen maç
     */
    public void macReddedildi(Mac mac) {
        if (mac == null) {
            logger.error("Null maç ile macReddedildi çağrıldı!");
            return;
        }
        
        logger.info("❌ Maç reddedildi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_REDDEDILDI";
        gozlemcileriBilgilendir();
    }
    
    /**
     * Maç başladığında çağrılır
     * 
     * Takipçilere "maç başladı" bildirimi gönderilir.
     * 
     * @param mac Başlayan maç
     */
    public void macBasladi(Mac mac) {
        if (mac == null) {
            logger.error("Null maç ile macBasladi çağrıldı!");
            return;
        }
        
        logger.info("⚽ Maç başladı: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_BASLADI";
        gozlemcileriBilgilendir();
    }
    
    /**
     * Maç bittiğinde çağrılır
     * 
     * Takipçilere "maç bitti" bildirimi gönderilir.
     * 
     * @param mac Biten maç
     */
    public void macBitti(Mac mac) {
        if (mac == null) {
            logger.error("Null maç ile macBitti çağrıldı!");
            return;
        }
        
        logger.info("🏁 Maç bitti: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_BITTI";
        gozlemcileriBilgilendir();
    }
    
    /**
     * Gol atıldığında çağrılır
     * 
     * Takipçilere "gol atıldı" bildirimi gönderilir.
     * 
     * @param mac Gol atılan maç
     */
    public void golAtildi(Mac mac) {
        if (mac == null) {
            logger.error("Null maç ile golAtildi çağrıldı!");
            return;
        }
        
        logger.info("⚽ Gol atıldı: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "GOL_ATILDI";
        gozlemcileriBilgilendir();
    }
    
    /**
     * Yeni yorum eklendiğinde çağrılır
     * 
     * Takipçilere "yeni yorum" bildirimi gönderilir.
     * 
     * @param mac Yorum eklenen maç
     */
    public void yeniYorum(Mac mac) {
        if (mac == null) {
            logger.error("Null maç ile yeniYorum çağrıldı!");
            return;
        }
        
        logger.info("💬 Yeni yorum eklendi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "YENI_YORUM";
        gozlemcileriBilgilendir();
    }
    
    /**
     * Mevcut gözlemci sayısını döndürür
     * 
     * @return Kayıtlı gözlemci sayısı
     */
    public int getGozlemciSayisi() {
        return gozlemciler.size();
    }
    
    /**
     * Tüm gözlemcileri temizler
     * 
     * Genellikle test senaryolarında veya sistem yeniden başlatılırken kullanılır.
     */
    public void tumGozlemcileriTemizle() {
        int oncekiSayi = gozlemciler.size();
        gozlemciler.clear();
        logger.info("🗑️ Tüm gözlemciler temizlendi: {} gözlemci silindi", oncekiSayi);
    }
    
    /**
     * String temsili
     */
    @Override
    public String toString() {
        return "MacOnayKonusu{" +
                "gozlemciSayisi=" + gozlemciler.size() +
                ", aktifOlayTipi='" + aktifOlayTipi + '\'' +
                ", aktifMacId=" + (aktifMac != null ? aktifMac.getId() : "null") +
                '}';
    }
}

