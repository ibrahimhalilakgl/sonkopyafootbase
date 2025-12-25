package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Organizasyon entity sınıfı
 * Maç organizasyonlarını temsil eder (lig, kupa vb.)
 */
@Entity
@Table(name = "organizasyonlar")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Organizasyon {

    /**
     * Organizasyonun benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organizasyon_id")
    private Long id;

    /**
     * Organizasyonun adı
     */
    @Column(nullable = false)
    private String ad;

    /**
     * Organizasyona ait maçlar
     */
    @JsonIgnore
    @OneToMany(mappedBy = "organizasyon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MacOrganizasyon> maclar = new ArrayList<>();

    /**
     * Varsayılan constructor
     */
    public Organizasyon() {
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

    public List<MacOrganizasyon> getMaclar() {
        return maclar;
    }

    public void setMaclar(List<MacOrganizasyon> maclar) {
        this.maclar = maclar;
    }
}

