package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Maç Puanı entity sınıfı
 * Bir maçın toplam puanını temsil eder
 */
@Entity
@Table(name = "mac_puanlari")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MacPuani {

    /**
     * Maç ID (Primary Key)
     */
    @Id
    @Column(name = "mac_id", nullable = false)
    private Long macId;

    /**
     * Maç (ilişki)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", insertable = false, updatable = false)
    private Mac mac;

    /**
     * Toplam puan (numeric(5,2))
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal puan;

    /**
     * Varsayılan constructor
     */
    public MacPuani() {
    }

    // Getter ve Setter metodları

    public Long getMacId() {
        return macId;
    }

    public void setMacId(Long macId) {
        this.macId = macId;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public BigDecimal getPuan() {
        return puan;
    }

    public void setPuan(BigDecimal puan) {
        this.puan = puan;
    }
}

