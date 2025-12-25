package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Oyuncu Yorumları entity sınıfı
 * Oyunculara yapılan yorumları temsil eder
 */
@Entity
@Table(name = "oyuncu_yorumlari")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OyuncuYorumlari {

    /**
     * Yorumun benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "yorum_id")
    private Long id;

    /**
     * Oyuncu
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id", nullable = false)
    private Oyuncu oyuncu;

    /**
     * Yorumu yazan kullanıcı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false, referencedColumnName = "kullanici_id")
    private Kullanici kullanici;

    /**
     * Yorum içeriği
     */
    @Column(name = "icerik", nullable = false, columnDefinition = "TEXT")
    private String icerik;

    /**
     * Oluşturma tarihi
     */
    @Column(name = "olusturma_tarihi", nullable = false)
    private LocalDateTime olusturmaTarihi;

    /**
     * Varsayılan constructor
     */
    public OyuncuYorumlari() {
        this.olusturmaTarihi = LocalDateTime.now();
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
}

