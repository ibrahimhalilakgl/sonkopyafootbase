package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stadyum entity sınıfı
 * Futbol stadyumlarını temsil eder
 */
@Entity
@Table(name = "stadyumlar")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Stadyum {

    /**
     * Stadyumun benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stadyum_id")
    private Long id;

    /**
     * Stadyumun adı
     */
    @Column(name = "stadyum_adi", nullable = false, unique = true)
    private String stadyumAdi;

    /**
     * Stadyumun şehri
     */
    @Column(name = "sehir", nullable = false)
    private String sehir;

    /**
     * Stadyumun ülkesi
     */
    @Column(name = "ulke", nullable = false)
    private String ulke;

    /**
     * Stadyumda oynayan takımlar
     */
    @JsonIgnore
    @OneToMany(mappedBy = "stadyum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Takim> takimlar = new ArrayList<>();

    /**
     * Varsayılan constructor
     */
    public Stadyum() {
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStadyumAdi() {
        return stadyumAdi;
    }

    public void setStadyumAdi(String stadyumAdi) {
        this.stadyumAdi = stadyumAdi;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getUlke() {
        return ulke;
    }

    public void setUlke(String ulke) {
        this.ulke = ulke;
    }

    public List<Takim> getTakimlar() {
        return takimlar;
    }

    public void setTakimlar(List<Takim> takimlar) {
        this.takimlar = takimlar;
    }
}

