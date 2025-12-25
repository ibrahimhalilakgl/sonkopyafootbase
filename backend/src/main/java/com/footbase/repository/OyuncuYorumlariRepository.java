package com.footbase.repository;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Oyuncu;
import com.footbase.entity.OyuncuYorumlari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Oyuncu Yorumları repository interface'i
 * Oyuncu yorumları veritabanı işlemlerini yönetir
 */
@Repository
public interface OyuncuYorumlariRepository extends JpaRepository<OyuncuYorumlari, Long> {

    /**
     * Oyuncuya göre yorumları bulur
     * @param oyuncu Oyuncu
     * @return Oyuncuya ait yorumlar listesi
     */
    @Query("SELECT DISTINCT oy FROM OyuncuYorumlari oy " +
           "LEFT JOIN FETCH oy.kullanici " +
           "WHERE oy.oyuncu = :oyuncu " +
           "ORDER BY oy.olusturmaTarihi DESC")
    List<OyuncuYorumlari> findByOyuncuOrderByOlusturmaTarihiDesc(Oyuncu oyuncu);

    /**
     * Oyuncu ID'sine göre yorumları bulur
     * @param oyuncuId Oyuncu ID'si
     * @return Oyuncuya ait yorumlar listesi
     */
    @Query("SELECT DISTINCT oy FROM OyuncuYorumlari oy " +
           "LEFT JOIN FETCH oy.kullanici " +
           "WHERE oy.oyuncu.id = :oyuncuId " +
           "ORDER BY oy.olusturmaTarihi DESC")
    List<OyuncuYorumlari> findByOyuncuIdOrderByOlusturmaTarihiDesc(Long oyuncuId);

    /**
     * Kullanıcıya göre oyuncu yorumlarını bulur (tarihe göre sıralı)
     * @param kullanici Kullanıcı
     * @return Kullanıcıya ait oyuncu yorumları listesi
     */
    @Query("SELECT DISTINCT oy FROM OyuncuYorumlari oy " +
           "LEFT JOIN FETCH oy.oyuncu " +
           "WHERE oy.kullanici = :kullanici " +
           "ORDER BY oy.olusturmaTarihi DESC")
    List<OyuncuYorumlari> findByKullaniciOrderByOlusturmaTarihiDesc(Kullanici kullanici);
}

