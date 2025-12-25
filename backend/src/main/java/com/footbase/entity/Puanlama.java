package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Puanlama entity sınıfı
 * Maçlara yapılan puanlamaları temsil eder (mac_puanlamalari tablosu)
 */
@Entity
@Table(name = "mac_puanlamalari")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Puanlama {

    /**
     * Puanlamanın benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Puan (0-100 arası)
     */
    @Column(nullable = false)
    private Integer puan;

    /**
     * Ağırlık (1-3 arası)
     */
    @Column(nullable = false)
    private Integer agirlik = 1;

    /**
     * Puanlamanın yapılma tarihi (veritabanında yok, @Transient)
     */
    @Transient
    private LocalDateTime puanlamaTarihi;

    /**
     * Puanlamayı yapan kullanıcı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false, referencedColumnName = "kullanici_id")
    private Kullanici kullanici;

    /**
     * Puanlanan maç
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    /**
     * Puanlanan oyuncu (veritabanında yok, şimdilik null)
     */
    @Transient
    private Oyuncu oyuncu;

    /**
     * Varsayılan constructor
     */
    public Puanlama() {
        this.puanlamaTarihi = LocalDateTime.now();
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPuan() {
        return puan;
    }

    public void setPuan(Integer puan) {
        this.puan = puan;
    }

    public Integer getAgirlik() {
        return agirlik;
    }

    public void setAgirlik(Integer agirlik) {
        this.agirlik = agirlik;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public LocalDateTime getPuanlamaTarihi() {
        return puanlamaTarihi;
    }

    public void setPuanlamaTarihi(LocalDateTime puanlamaTarihi) {
        this.puanlamaTarihi = puanlamaTarihi;
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }
}

