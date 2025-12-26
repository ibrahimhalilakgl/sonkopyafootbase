package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Maç entity sınıfı
 * Futbol maçlarını temsil eder
 */
@Entity
@Table(name = "maclar")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Mac {

    /**
     * Maçın benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ev sahibi takım (mac_takimlari tablosundan hesaplanır)
     */
    @Transient
    private Takim evSahibiTakim;

    /**
     * Deplasman takımı (mac_takimlari tablosundan hesaplanır)
     */
    @Transient
    private Takim deplasmanTakim;

    /**
     * Ev sahibi takımın skoru (mac_takimlari tablosundan hesaplanır)
     */
    @Transient
    private Integer evSahibiSkor;

    /**
     * Deplasman takımın skoru (mac_takimlari tablosundan hesaplanır)
     */
    @Transient
    private Integer deplasmanSkor;

    /**
     * Maça ait takımlar (mac_takimlari tablosu)
     */
    @JsonIgnore
    @OneToMany(mappedBy = "mac", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MacTakimlari> macTakimlari = new ArrayList<>();

    /**
     * Maçın tarihi
     */
    @Column(nullable = false)
    private java.time.LocalDate tarih;

    /**
     * Maçın saati
     */
    @Column(nullable = false)
    private java.time.LocalTime saat;

    /**
     * Maçın durumu (Planlandı, Devam Ediyor, Bitti, İptal)
     * Not: Veritabanında durum kolonu yok, bu yüzden nullable
     */
    @Transient
    private String durum = "Planlandı";

    /**
     * Maçın onay durumu (ONAY_BEKLIYOR, YAYINDA, REDDEDILDI)
     * Not: Veritabanında kolonu yok, @Transient olarak ekleniyor
     */
    @Transient
    private String onayDurumu = "ONAY_BEKLIYOR";

    /**
     * Maçı ekleyen editör
     * Not: Veritabanında kolonu yok, @Transient olarak ekleniyor
     */
    @Transient
    private Kullanici editor;

    /**
     * Editör ID'si (veritabanında kolonu yok)
     */
    @Transient
    private Long editorId;

    /**
     * Maçın hakemi
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hakem_id")
    private Hakem hakem;

    /**
     * Maçın stadyumu (ev sahibi takımın stadyumundan hesaplanır)
     */
    @Transient
    private Stadyum stadyum;

    /**
     * Maçın ligi (takımlardan hesaplanır)
     * Not: Veritabanında lig_id kolonu yok, bu yüzden @Transient
     */
    @Transient
    private Lig lig;

    /**
     * Maçın organizasyonu (Süper Lig, Champions League vb.)
     * Not: Veritabanında organizasyon_id kolonu yok, bu yüzden @Transient
     */
    @Transient
    private Organizasyon organizasyon;

    /**
     * Maç notu/açıklaması
     * Not: Veritabanında 'not' kolonu yok, bu yüzden @Transient
     */
    @Transient
    private String not;

    /**
     * Maça ait yorumlar
     */
    @JsonIgnore
    @OneToMany(mappedBy = "mac", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Yorum> yorumlar = new ArrayList<>();

    /**
     * Maça ait puanlamalar
     */
    @JsonIgnore
    @OneToMany(mappedBy = "mac", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Puanlama> puanlamalar = new ArrayList<>();

    /**
     * Varsayılan constructor
     */
    public Mac() {
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Takim getEvSahibiTakim() {
        return evSahibiTakim;
    }

    public void setEvSahibiTakim(Takim evSahibiTakim) {
        this.evSahibiTakim = evSahibiTakim;
    }

    public Takim getDeplasmanTakim() {
        return deplasmanTakim;
    }

    public void setDeplasmanTakim(Takim deplasmanTakim) {
        this.deplasmanTakim = deplasmanTakim;
    }

    public Integer getEvSahibiSkor() {
        return evSahibiSkor;
    }

    public void setEvSahibiSkor(Integer evSahibiSkor) {
        this.evSahibiSkor = evSahibiSkor;
    }

    public Integer getDeplasmanSkor() {
        return deplasmanSkor;
    }

    public void setDeplasmanSkor(Integer deplasmanSkor) {
        this.deplasmanSkor = deplasmanSkor;
    }

    public java.time.LocalDate getTarih() {
        return tarih;
    }

    public void setTarih(java.time.LocalDate tarih) {
        this.tarih = tarih;
    }

    public java.time.LocalTime getSaat() {
        return saat;
    }

    public void setSaat(java.time.LocalTime saat) {
        this.saat = saat;
    }

    /**
     * Maç tarihi ve saatini LocalDateTime olarak döndürür (yardımcı metod)
     */
    public LocalDateTime getMacTarihi() {
        if (tarih != null && saat != null) {
            return LocalDateTime.of(tarih, saat);
        }
        return null;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public Hakem getHakem() {
        return hakem;
    }

    public void setHakem(Hakem hakem) {
        this.hakem = hakem;
    }

    public List<Yorum> getYorumlar() {
        return yorumlar;
    }

    public void setYorumlar(List<Yorum> yorumlar) {
        this.yorumlar = yorumlar;
    }

    public List<Puanlama> getPuanlamalar() {
        return puanlamalar;
    }

    public void setPuanlamalar(List<Puanlama> puanlamalar) {
        this.puanlamalar = puanlamalar;
    }

    public List<MacTakimlari> getMacTakimlari() {
        return macTakimlari;
    }

    public void setMacTakimlari(List<MacTakimlari> macTakimlari) {
        this.macTakimlari = macTakimlari;
    }

    public String getOnayDurumu() {
        return onayDurumu;
    }

    public void setOnayDurumu(String onayDurumu) {
        this.onayDurumu = onayDurumu;
    }

    public Kullanici getEditor() {
        return editor;
    }

    public void setEditor(Kullanici editor) {
        this.editor = editor;
        if (editor != null) {
            this.editorId = editor.getId();
        }
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
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

    public Organizasyon getOrganizasyon() {
        return organizasyon;
    }

    public void setOrganizasyon(Organizasyon organizasyon) {
        this.organizasyon = organizasyon;
    }

    public String getNot() {
        return not;
    }

    public void setNot(String not) {
        this.not = not;
    }
}

