package com.footbase.repository;

import com.footbase.entity.Lig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Lig repository interface'i
 * Lig veritabanı işlemlerini yönetir
 */
@Repository
public interface LigRepository extends JpaRepository<Lig, Long> {

    /**
     * Lig adına göre arama yapar
     * @param ligAdi Lig adı
     * @return Bulunan lig
     */
    Optional<Lig> findByLigAdi(String ligAdi);

    /**
     * Ülkeye göre ligleri getirir
     * @param ulke Ülke
     * @return Lig listesi
     */
    List<Lig> findByUlke(String ulke);

    /**
     * Lig adı var mı kontrol eder
     * @param ligAdi Lig adı
     * @return Varsa true, yoksa false
     */
    boolean existsByLigAdi(String ligAdi);

    /**
     * Lig adına göre like araması yapar
     * @param ligAdi Aranacak kelime
     * @return Bulunan ligler
     */
    @Query("SELECT l FROM Lig l WHERE LOWER(l.ligAdi) LIKE LOWER(CONCAT('%', :ligAdi, '%'))")
    List<Lig> searchByLigAdi(String ligAdi);
}

