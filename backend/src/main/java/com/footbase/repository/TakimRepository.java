package com.footbase.repository;

import com.footbase.entity.Takim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Takım repository interface'i
 * Takım veritabanı işlemlerini yönetir
 */
@Repository
public interface TakimRepository extends JpaRepository<Takim, Long> {

    /**
     * Tüm takımları ilişkili entity'lerle birlikte getirir
     * @return Takım listesi
     */
    @Query("SELECT DISTINCT t FROM Takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "LEFT JOIN FETCH t.teknikDirektor")
    List<Takim> findAll();

    /**
     * ID'ye göre takım getirir (ilişkili entity'lerle birlikte)
     * @param id Takım ID'si
     * @return Takım
     */
    @Query("SELECT DISTINCT t FROM Takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "LEFT JOIN FETCH t.teknikDirektor " +
           "WHERE t.id = :id")
    java.util.Optional<Takim> findById(Long id);

    /**
     * Takım adına göre takım bulur
     * @param ad Takım adı
     * @return Bulunan takım
     */
    @Query("SELECT DISTINCT t FROM Takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "LEFT JOIN FETCH t.teknikDirektor " +
           "WHERE t.ad = :ad")
    Optional<Takim> findByAd(String ad);

    /**
     * Takım adının var olup olmadığını kontrol eder
     * @param ad Takım adı
     * @return Takım adı varsa true, yoksa false
     */
    boolean existsByAd(String ad);
}


