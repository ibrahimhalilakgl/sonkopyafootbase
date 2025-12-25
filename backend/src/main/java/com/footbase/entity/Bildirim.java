package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Bildirim Entity Sınıfı
 * 
 * Bu sınıf, sistemdeki tüm bildirimleri temsil eder.
 * Observer Pattern'in somut uygulamasıdır - gözlemcilere gönderilen
 * bildirimler veritabanında saklanır ve kullanıcılar tarafından görüntülenebilir.
 * 
 * Örnek Kullanım Senaryoları:
 * - Editör maç eklediğinde admin'e bildirim
 * - Admin maçı onayladığında editöre bildirim
 * - Maç başladığında takipçilere bildirim
 * - Yeni yorum geldiğinde maç sahibine bildirim
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Entity
@Table(name = "bildirimler")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bildirim {

    /**
     * Bildirimin benzersiz kimliği (Primary Key)
     * Otomatik olarak veritabanı tarafından arttırılır
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Bildirimi alan kullanıcı
     * ManyToOne ilişki: Bir kullanıcı birden fazla bildirim alabilir
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alici_kullanici_id", nullable = false)
    private Kullanici aliciKullanici;

    /**
     * Bildirimi tetikleyen kullanıcı (opsiyonel)
     * Örnek: Editör maç eklediğinde, editör gönderici olur
     * Null olabilir (sistem bildirimleri için)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gonderici_kullanici_id")
    private Kullanici gondericiKullanici;

    /**
     * Bildirim tipi
     * Bildirimin türünü belirler ve Observer Pattern'de event type olarak kullanılır
     * 
     * Olası değerler:
     * - MAC_EKLENDI: Yeni maç eklendi (admin için)
     * - MAC_ONAYLANDI: Maç onaylandı (editör için)
     * - MAC_REDDEDILDI: Maç reddedildi (editör için)
     * - MAC_BASLADI: Maç başladı (takipçiler için)
     * - MAC_BITTI: Maç bitti (takipçiler için)
     * - YENI_YORUM: Yeni yorum eklendi
     * - YENI_PUANLAMA: Oyuncu puanlandı
     * - GOL_ATILDI: Gol olayı
     */
    @Column(name = "bildirim_tipi", nullable = false, length = 50)
    private String bildirimTipi;

    /**
     * Bildirim başlığı
     * Kısa ve öz bilgi (max 255 karakter)
     * Örnek: "Yeni Maç Onay Bekliyor"
     */
    @Column(name = "baslik", nullable = false)
    private String baslik;

    /**
     * Bildirim içeriği
     * Detaylı açıklama (max 1000 karakter)
     * Örnek: "Editör Ahmet Yılmaz tarafından 'Galatasaray vs Fenerbahçe' maçı eklendi ve onayınızı bekliyor."
     */
    @Column(name = "icerik", nullable = false, length = 1000)
    private String icerik;

    /**
     * İlgili maç (opsiyonel)
     * Bildirim bir maçla ilgiliyse bu alan doldurulur
     * Null olabilir (genel bildirimler için)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id")
    private Mac mac;

    /**
     * İlgili oyuncu (opsiyonel)
     * Bildirim bir oyuncuyla ilgiliyse bu alan doldurulur
     * Null olabilir
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id")
    private Oyuncu oyuncu;

    /**
     * Okundu mu?
     * Kullanıcı bildirimi görüntülediyse true, görüntülemediyse false
     * Varsayılan: false
     */
    @Column(name = "okundu", nullable = false)
    private Boolean okundu = false;

    /**
     * Bildirimin oluşturulma zamanı
     * Otomatik olarak şu anki zaman atanır
     */
    @Column(name = "olusturma_zamani", nullable = false)
    private LocalDateTime olusturmaZamani;

    /**
     * Bildirimin okunma zamanı
     * Kullanıcı bildirimi okuduğunda güncellenir
     * Null olabilir (henüz okunmamışsa)
     */
    @Column(name = "okunma_zamani")
    private LocalDateTime okunmaZamani;

    /**
     * Bildirime tıklandığında yönlendirilecek URL
     * Frontend'de bildirime tıklanınca bu sayfaya yönlendirilir
     * Örnek: "/app/matches/123"
     */
    @Column(name = "hedef_url", length = 500)
    private String hedefUrl;

    /**
     * Varsayılan constructor
     * Oluşturma zamanını otomatik olarak şu anki zamana ayarlar
     */
    public Bildirim() {
        this.olusturmaZamani = LocalDateTime.now();
        this.okundu = false;
    }

    /**
     * Parametreli constructor
     * Hızlı bildirim oluşturmak için kullanılır
     * 
     * @param aliciKullanici Bildirimi alacak kullanıcı
     * @param bildirimTipi Bildirim tipi (MAC_EKLENDI, YENI_YORUM vb.)
     * @param baslik Bildirim başlığı
     * @param icerik Bildirim içeriği
     */
    public Bildirim(Kullanici aliciKullanici, String bildirimTipi, String baslik, String icerik) {
        this();
        this.aliciKullanici = aliciKullanici;
        this.bildirimTipi = bildirimTipi;
        this.baslik = baslik;
        this.icerik = icerik;
    }

    // ==================== GETTER VE SETTER METODLARI ====================

    /**
     * Bildirim ID'sini döndürür
     * @return Bildirim ID'si
     */
    public Long getId() {
        return id;
    }

    /**
     * Bildirim ID'sini ayarlar
     * @param id Yeni bildirim ID'si
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Alıcı kullanıcıyı döndürür
     * @return Bildirimi alan kullanıcı
     */
    public Kullanici getAliciKullanici() {
        return aliciKullanici;
    }

    /**
     * Alıcı kullanıcıyı ayarlar
     * @param aliciKullanici Bildirimi alacak kullanıcı
     */
    public void setAliciKullanici(Kullanici aliciKullanici) {
        this.aliciKullanici = aliciKullanici;
    }

    /**
     * Gönderici kullanıcıyı döndürür
     * @return Bildirimi gönderen kullanıcı (null olabilir)
     */
    public Kullanici getGondericiKullanici() {
        return gondericiKullanici;
    }

    /**
     * Gönderici kullanıcıyı ayarlar
     * @param gondericiKullanici Bildirimi gönderen kullanıcı
     */
    public void setGondericiKullanici(Kullanici gondericiKullanici) {
        this.gondericiKullanici = gondericiKullanici;
    }

    /**
     * Bildirim tipini döndürür
     * @return Bildirim tipi (MAC_EKLENDI, YENI_YORUM vb.)
     */
    public String getBildirimTipi() {
        return bildirimTipi;
    }

    /**
     * Bildirim tipini ayarlar
     * @param bildirimTipi Yeni bildirim tipi
     */
    public void setBildirimTipi(String bildirimTipi) {
        this.bildirimTipi = bildirimTipi;
    }

    /**
     * Bildirim başlığını döndürür
     * @return Bildirim başlığı
     */
    public String getBaslik() {
        return baslik;
    }

    /**
     * Bildirim başlığını ayarlar
     * @param baslik Yeni başlık
     */
    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    /**
     * Bildirim içeriğini döndürür
     * @return Bildirim içeriği
     */
    public String getIcerik() {
        return icerik;
    }

    /**
     * Bildirim içeriğini ayarlar
     * @param icerik Yeni içerik
     */
    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    /**
     * İlgili maçı döndürür
     * @return İlgili maç (null olabilir)
     */
    public Mac getMac() {
        return mac;
    }

    /**
     * İlgili maçı ayarlar
     * @param mac İlgili maç
     */
    public void setMac(Mac mac) {
        this.mac = mac;
    }

    /**
     * İlgili oyuncuyu döndürür
     * @return İlgili oyuncu (null olabilir)
     */
    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    /**
     * İlgili oyuncuyu ayarlar
     * @param oyuncu İlgili oyuncu
     */
    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }

    /**
     * Bildirimin okunup okunmadığını döndürür
     * @return true ise okundu, false ise okunmadı
     */
    public Boolean getOkundu() {
        return okundu;
    }

    /**
     * Bildirimin okundu durumunu ayarlar
     * @param okundu true ise okundu olarak işaretle
     */
    public void setOkundu(Boolean okundu) {
        this.okundu = okundu;
        // Eğer okundu olarak işaretleniyorsa, okunma zamanını güncelle
        if (okundu && this.okunmaZamani == null) {
            this.okunmaZamani = LocalDateTime.now();
        }
    }

    /**
     * Oluşturma zamanını döndürür
     * @return Bildirimin oluşturulma zamanı
     */
    public LocalDateTime getOlusturmaZamani() {
        return olusturmaZamani;
    }

    /**
     * Oluşturma zamanını ayarlar
     * @param olusturmaZamani Yeni oluşturma zamanı
     */
    public void setOlusturmaZamani(LocalDateTime olusturmaZamani) {
        this.olusturmaZamani = olusturmaZamani;
    }

    /**
     * Okunma zamanını döndürür
     * @return Bildirimin okunma zamanı (null olabilir)
     */
    public LocalDateTime getOkunmaZamani() {
        return okunmaZamani;
    }

    /**
     * Okunma zamanını ayarlar
     * @param okunmaZamani Yeni okunma zamanı
     */
    public void setOkunmaZamani(LocalDateTime okunmaZamani) {
        this.okunmaZamani = okunmaZamani;
    }

    /**
     * Hedef URL'i döndürür
     * @return Bildirime tıklanınca yönlendirilecek URL
     */
    public String getHedefUrl() {
        return hedefUrl;
    }

    /**
     * Hedef URL'i ayarlar
     * @param hedefUrl Yönlendirilecek URL
     */
    public void setHedefUrl(String hedefUrl) {
        this.hedefUrl = hedefUrl;
    }

    /**
     * Bildirimi okundu olarak işaretler
     * Okunma zamanını otomatik olarak günceller
     */
    public void okunduOlarakIsaretle() {
        this.okundu = true;
        this.okunmaZamani = LocalDateTime.now();
    }

    /**
     * Bildirimin okunmamış olup olmadığını kontrol eder
     * @return true ise okunmamış
     */
    public boolean okunmamisMi() {
        return !this.okundu;
    }

    /**
     * String temsili
     * @return Bildirimin string temsili
     */
    @Override
    public String toString() {
        return "Bildirim{" +
                "id=" + id +
                ", bildirimTipi='" + bildirimTipi + '\'' +
                ", baslik='" + baslik + '\'' +
                ", okundu=" + okundu +
                ", olusturmaZamani=" + olusturmaZamani +
                '}';
    }
}

