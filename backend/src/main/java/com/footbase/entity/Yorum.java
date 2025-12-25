package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Yorum entity sınıfı
 * Maçlara yapılan yorumları temsil eder
 */
@Entity
@Table(name = "yorumlar")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Yorum {

    /**
     * Yorumun benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "yorum_id")
    private Long id;

    /**
     * Yorumun içeriği
     */
    @Column(name = "icerik", nullable = false, columnDefinition = "TEXT")
    private String mesaj;

    /**
     * Yorumun yazılma tarihi
     */
    @Column(name = "olusturma_tarihi", nullable = false)
    private LocalDateTime yorumTarihi;

    /**
     * Yorum tipi (USER, EDITOR)
     */
    @Column(name = "yorum_tipi", nullable = false)
    private String yorumTipi = "USER";

    /**
     * Yorumu yazan kullanıcı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false, referencedColumnName = "kullanici_id")
    private Kullanici kullanici;

    /**
     * Yorumun ait olduğu maç
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    // Yorum beğenileri için ayrı tablo yok, bu özellik şimdilik kaldırıldı
    @Transient
    private List<Kullanici> begenenKullanicilar = new ArrayList<>();

    /**
     * Varsayılan constructor
     */
    public Yorum() {
        this.yorumTarihi = LocalDateTime.now();
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public LocalDateTime getYorumTarihi() {
        return yorumTarihi;
    }

    public void setYorumTarihi(LocalDateTime yorumTarihi) {
        this.yorumTarihi = yorumTarihi;
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public List<Kullanici> getBegenenKullanicilar() {
        return begenenKullanicilar;
    }

    public void setBegenenKullanicilar(List<Kullanici> begenenKullanicilar) {
        this.begenenKullanicilar = begenenKullanicilar;
    }

    public String getYorumTipi() {
        return yorumTipi;
    }

    public void setYorumTipi(String yorumTipi) {
        this.yorumTipi = yorumTipi;
    }
}

