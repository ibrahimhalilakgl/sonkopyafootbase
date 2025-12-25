package com.footbase.entity;

// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Takım-Teknik Direktör ilişki entity sınıfı
 * Bir takımın teknik direktör geçmişini temsil eder
 * NOT: Bu tablo veritabanında yok, şimdilik kullanılmıyor
 */
// @Entity
// @Table(name = "takim_teknik_direktor")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TakimTeknikDirektor {

    /**
     * İlişkinin benzersiz kimliği (veritabanında tablo yok, @Transient benzeri)
     */
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Takım (veritabanında tablo yok, @Transient benzeri)
     */
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "takim_id", nullable = false)
    private Takim takim;

    /**
     * Teknik direktör (veritabanında tablo yok, @Transient benzeri)
     */
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "td_id", nullable = false)
    private TeknikDirektor teknikDirektor;

    /**
     * Başlangıç tarihi (veritabanında tablo yok, @Transient benzeri)
     */
    // @Column(name = "baslangic_tarihi", nullable = false)
    private LocalDate baslangicTarihi;

    /**
     * Bitiş tarihi (null ise hala devam ediyor) (veritabanında tablo yok, @Transient benzeri)
     */
    // @Column(name = "bitis_tarihi")
    private LocalDate bitisTarihi;

    /**
     * Varsayılan constructor
     */
    public TakimTeknikDirektor() {
    }

    // Getter ve Setter metodları

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Takim getTakim() {
        return takim;
    }

    public void setTakim(Takim takim) {
        this.takim = takim;
    }

    public TeknikDirektor getTeknikDirektor() {
        return teknikDirektor;
    }

    public void setTeknikDirektor(TeknikDirektor teknikDirektor) {
        this.teknikDirektor = teknikDirektor;
    }

    public LocalDate getBaslangicTarihi() {
        return baslangicTarihi;
    }

    public void setBaslangicTarihi(LocalDate baslangicTarihi) {
        this.baslangicTarihi = baslangicTarihi;
    }

    public LocalDate getBitisTarihi() {
        return bitisTarihi;
    }

    public void setBitisTarihi(LocalDate bitisTarihi) {
        this.bitisTarihi = bitisTarihi;
    }
}

