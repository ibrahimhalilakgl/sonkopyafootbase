package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Oyuncu entity sınıfı
 * Futbol oyuncularını temsil eder
 */
@Entity
@Table(name = "oyuncular")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Oyuncu {

    /**
     * Oyuncunun benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Oyuncunun adı
     */
    @Column(nullable = false)
    private String ad;

    /**
     * Oyuncunun soyadı
     */
    @Column(nullable = false)
    private String soyad;

    /**
     * Oyuncunun pozisyonu (KALECI, DEFANS, ORTA_SAHA, FORVET)
     */
    @Column(name = "mevki", nullable = false)
    private String pozisyon;

    /**
     * Oyuncunun doğum tarihi (veritabanında yok, @Transient)
     */
    @Transient
    private java.time.LocalDate dogumTarihi;

    /**
     * Oyuncunun milliyeti
     */
    @Column(name = "ulke", nullable = false)
    private String milliyet;

    /**
     * Oyuncunun fotoğrafı URL'i
     */
    @Column(name = "foto_url")
    private String fotograf;

    /**
     * Oyuncunun ait olduğu takım
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "takim_id")
    private Takim takim;

    // Oyuncu puanlamaları veritabanında oyuncu_yorumlari tablosunda
    @Transient
    private List<Puanlama> puanlamalar = new ArrayList<>();

    /**
     * Varsayılan constructor
     */
    public Oyuncu() {
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getPozisyon() {
        return pozisyon;
    }

    public void setPozisyon(String pozisyon) {
        this.pozisyon = pozisyon;
    }

    public java.time.LocalDate getDogumTarihi() {
        return dogumTarihi;
    }

    public void setDogumTarihi(java.time.LocalDate dogumTarihi) {
        this.dogumTarihi = dogumTarihi;
    }

    /**
     * Yaşı hesaplar (doğum tarihinden)
     */
    @Transient
    public Integer getYas() {
        if (dogumTarihi != null) {
            return java.time.LocalDate.now().getYear() - dogumTarihi.getYear();
        }
        return null;
    }

    public void setYas(Integer yas) {
        // Yaş set edilemez, sadece doğum tarihi set edilir
    }

    public String getMilliyet() {
        return milliyet;
    }

    public void setMilliyet(String milliyet) {
        this.milliyet = milliyet;
    }

    public String getFotograf() {
        return fotograf;
    }

    public void setFotograf(String fotograf) {
        this.fotograf = fotograf;
    }

    public Takim getTakim() {
        return takim;
    }

    public void setTakim(Takim takim) {
        this.takim = takim;
    }

    public List<Puanlama> getPuanlamalar() {
        return puanlamalar;
    }

    public void setPuanlamalar(List<Puanlama> puanlamalar) {
        this.puanlamalar = puanlamalar;
    }
}

