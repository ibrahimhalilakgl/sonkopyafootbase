package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lig entity sınıfı
 * Futbol liglerini temsil eder
 */
@Entity
@Table(name = "ligler")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lig {

    /**
     * Ligin benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lig_id")
    private Long id;

    /**
     * Ligin adı
     */
    @Column(name = "lig_adi", nullable = false, unique = true)
    private String ligAdi;

    /**
     * Ligin ülkesi
     */
    @Column(name = "ulke", nullable = false)
    private String ulke;

    /**
     * Lige ait takımlar
     */
    @JsonIgnore
    @OneToMany(mappedBy = "lig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Takim> takimlar = new ArrayList<>();

    /**
     * Varsayılan constructor
     */
    public Lig() {
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLigAdi() {
        return ligAdi;
    }

    public void setLigAdi(String ligAdi) {
        this.ligAdi = ligAdi;
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

