package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Maç Takımları entity sınıfı
 * Maçlarda yer alan takımları ve skorlarını temsil eder
 */
@Entity
@Table(name = "mac_takimlari")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MacTakimlari {

    /**
     * Kaydın benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Maç
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    /**
     * Takım
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "takim_id", nullable = false)
    private Takim takim;

    /**
     * Ev sahibi mi?
     */
    @Column(name = "ev_sahibi", nullable = false)
    private Boolean evSahibi;

    /**
     * Takımın skoru
     */
    @Column(nullable = false)
    private Integer skor = 0;

    /**
     * Varsayılan constructor
     */
    public MacTakimlari() {
        this.skor = 0;
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public Takim getTakim() {
        return takim;
    }

    public void setTakim(Takim takim) {
        this.takim = takim;
    }

    public Boolean getEvSahibi() {
        return evSahibi;
    }

    /**
     * Ev sahibi mi? (Boolean helper method for stream filters)
     * @return Ev sahibiyse true
     */
    public boolean isEvSahibi() {
        return evSahibi != null && evSahibi;
    }

    public void setEvSahibi(Boolean evSahibi) {
        this.evSahibi = evSahibi;
    }

    public Integer getSkor() {
        return skor;
    }

    public void setSkor(Integer skor) {
        this.skor = skor;
    }
}

