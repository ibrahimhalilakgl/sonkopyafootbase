package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Oyuncu Puanları entity sınıfı
 * Oyuncuların ortalama puanlarını temsil eder
 */
@Entity
@Table(name = "oyuncu_puanlari")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OyuncuPuanlari {

    /**
     * Oyuncu ID (Primary Key)
     */
    @Id
    @Column(name = "oyuncu_id", nullable = false)
    private Long oyuncuId;
    
    /**
     * Oyuncu (ilişki)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id", insertable = false, updatable = false)
    private Oyuncu oyuncu;

    /**
     * Ortalama puan
     */
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal puan;

    /**
     * Varsayılan constructor
     */
    public OyuncuPuanlari() {
    }

    // Getter ve Setter metodları

    public Long getOyuncuId() {
        return oyuncuId;
    }

    public void setOyuncuId(Long oyuncuId) {
        this.oyuncuId = oyuncuId;
    }

    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }

    public BigDecimal getPuan() {
        return puan;
    }

    public void setPuan(BigDecimal puan) {
        this.puan = puan;
    }
}

