package com.footbase.repository;

import com.footbase.entity.Oyuncu;
import com.footbase.entity.Takim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Oyuncu repository interface'i
 * Oyuncu veritabanı işlemlerini yönetir
 */
@Repository
public interface OyuncuRepository extends JpaRepository<Oyuncu, Long> {

    /**
     * Tüm oyuncuları takım bilgileriyle birlikte getirir
     * @return Oyuncu listesi
     */
    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim")
    List<Oyuncu> findAll();

    /**
     * ID'ye göre oyuncu getirir (takım bilgisiyle birlikte)
     * @param id Oyuncu ID'si
     * @return Oyuncu
     */
    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim WHERE o.id = :id")
    java.util.Optional<Oyuncu> findById(Long id);

    /**
     * Takıma göre oyuncuları bulur
     * @param takim Takım
     * @return Takıma ait oyuncular listesi
     */
    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim WHERE o.takim = :takim")
    List<Oyuncu> findByTakim(Takim takim);

    /**
     * Takım ID'sine göre oyuncuları bulur
     * @param takimId Takım ID'si
     * @return Takıma ait oyuncular listesi
     */
    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim WHERE o.takim.id = :takimId")
    List<Oyuncu> findByTakimId(Long takimId);

    /**
     * Pozisyona göre oyuncuları bulur
     * @param pozisyon Pozisyon
     * @return Pozisyona ait oyuncular listesi
     */
    @Query("SELECT DISTINCT o FROM Oyuncu o LEFT JOIN FETCH o.takim WHERE o.pozisyon = :pozisyon")
    List<Oyuncu> findByPozisyon(String pozisyon);
}


