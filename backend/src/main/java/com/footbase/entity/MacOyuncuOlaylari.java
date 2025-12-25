package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Maç Oyuncu Olayları entity sınıfı
 * Maçlarda oyuncuların gerçekleştirdiği olayları (gol, kart) temsil eder
 */
@Entity
@Table(name = "mac_oyuncu_olaylari")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MacOyuncuOlaylari {

    /**
     * Olayın benzersiz kimliği
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
     * Oyuncu
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id", nullable = false)
    private Oyuncu oyuncu;

    /**
     * Olay türü (GOL, SARI_KART, KIRMIZI_KART)
     */
    @Column(name = "olay_turu", nullable = false, length = 20)
    private String olayTuru;

    /**
     * Olayın gerçekleştiği dakika
     */
    @Column(name = "dakika")
    private Integer dakika;

    /**
     * Varsayılan constructor
     */
    public MacOyuncuOlaylari() {
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

    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }

    public String getOlayTuru() {
        return olayTuru;
    }

    public void setOlayTuru(String olayTuru) {
        this.olayTuru = olayTuru;
    }

    public Integer getDakika() {
        return dakika;
    }

    public void setDakika(Integer dakika) {
        this.dakika = dakika;
    }
    
    /**
     * olayTipi için alias getter (API uyumluluğu için)
     */
    public String getOlayTipi() {
        return olayTuru;
    }
    
    /**
     * olayTipi için alias setter (API uyumluluğu için)
     */
    public void setOlayTipi(String olayTipi) {
        this.olayTuru = olayTipi;
    }
}

