package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Teknik Direktör entity sınıfı
 * Futbol teknik direktörlerini temsil eder
 */
@Entity
@Table(name = "teknik_direktorler")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TeknikDirektor {

    /**
     * Teknik direktörün benzersiz kimliği
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "td_id")
    private Long id;

    /**
     * Teknik direktörün adı ve soyadı
     */
    @Column(name = "ad_soyad", nullable = false)
    private String adSoyad;

    /**
     * Teknik direktörün uyruğu
     */
    @Column(name = "uyruk")
    private String uyruk;

    /**
     * Teknik direktörün çalıştığı takımlar (geçmiş ve şu anki)
     * NOT: takim_teknik_direktor tablosu veritabanında yok, bu yüzden @Transient
     */
    @Transient
    private List<TakimTeknikDirektor> takimGecmisi = new ArrayList<>();

    /**
     * Varsayılan constructor
     */
    public TeknikDirektor() {
    }

    // Getter ve Setter metodları

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }

    public String getUyruk() {
        return uyruk;
    }

    public void setUyruk(String uyruk) {
        this.uyruk = uyruk;
    }

    public List<TakimTeknikDirektor> getTakimGecmisi() {
        return takimGecmisi;
    }

    public void setTakimGecmisi(List<TakimTeknikDirektor> takimGecmisi) {
        this.takimGecmisi = takimGecmisi;
    }
}

