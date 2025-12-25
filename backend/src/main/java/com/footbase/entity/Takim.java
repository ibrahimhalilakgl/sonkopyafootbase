package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Takım entity sınıfı
 * Futbol takımlarını temsil eder
 */
@Entity
@Table(name = "takimlar")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Takim {

    /**
     * Takımın benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Takımın adı
     */
    @Column(nullable = false, unique = true)
    private String ad;

    /**
     * Takımın logosu URL'i
     */
    @Column(name = "logo_url")
    private String logo;

    /**
     * Takımın kuruluş yılı
     */
    @Column(name = "kurulma_yili")
    private Integer kurulusYili;

    /**
     * Takım açıklaması
     */
    private String aciklama;

    /**
     * Takım renkleri (veritabanında yok, @Transient)
     */
    @Transient
    private String renkler;

    /**
     * Takım ID (veritabanında yok, @Transient - id zaten var)
     */
    @Transient
    private Long takimId;

    /**
     * Takımın stadyumu
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadyum_id")
    private Stadyum stadyum;

    /**
     * Takımın ligi
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lig_id")
    private Lig lig;

    /**
     * Teknik direktör adı (veritabanında yok, @Transient - teknikDirektor relation'ından alınabilir)
     */
    @Transient
    private String teknikDirektorAdi;

    /**
     * Teknik direktör (FK)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teknik_direktor_id")
    private TeknikDirektor teknikDirektor;

    /**
     * Takıma ait oyuncular
     */
    @JsonIgnore
    @OneToMany(mappedBy = "takim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Oyuncu> oyuncular = new ArrayList<>();

    /**
     * Takımın ev sahibi olduğu maçlar
     */
    @Transient
    private List<Mac> evSahibiMaclar = new ArrayList<>();

    /**
     * Takımın deplasman olduğu maçlar
     */
    @Transient
    private List<Mac> deplasmanMaclar = new ArrayList<>();

    /**
     * Varsayılan constructor
     */
    public Takim() {
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getKurulusYili() {
        return kurulusYili;
    }

    public void setKurulusYili(Integer kurulusYili) {
        this.kurulusYili = kurulusYili;
    }

    public Long getTakimId() {
        return takimId;
    }

    public void setTakimId(Long takimId) {
        this.takimId = takimId;
    }

    public Stadyum getStadyum() {
        return stadyum;
    }

    public void setStadyum(Stadyum stadyum) {
        this.stadyum = stadyum;
    }

    public Lig getLig() {
        return lig;
    }

    public void setLig(Lig lig) {
        this.lig = lig;
    }

    public String getTeknikDirektorAdi() {
        return teknikDirektorAdi;
    }

    public void setTeknikDirektorAdi(String teknikDirektorAdi) {
        this.teknikDirektorAdi = teknikDirektorAdi;
    }

    public TeknikDirektor getTeknikDirektor() {
        return teknikDirektor;
    }

    public void setTeknikDirektor(TeknikDirektor teknikDirektor) {
        this.teknikDirektor = teknikDirektor;
    }

    public List<Oyuncu> getOyuncular() {
        return oyuncular;
    }

    public void setOyuncular(List<Oyuncu> oyuncular) {
        this.oyuncular = oyuncular;
    }

    public List<Mac> getEvSahibiMaclar() {
        return evSahibiMaclar;
    }

    public void setEvSahibiMaclar(List<Mac> evSahibiMaclar) {
        this.evSahibiMaclar = evSahibiMaclar;
    }

    public List<Mac> getDeplasmanMaclar() {
        return deplasmanMaclar;
    }

    public void setDeplasmanMaclar(List<Mac> deplasmanMaclar) {
        this.deplasmanMaclar = deplasmanMaclar;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getRenkler() {
        return renkler;
    }

    public void setRenkler(String renkler) {
        this.renkler = renkler;
    }
}
