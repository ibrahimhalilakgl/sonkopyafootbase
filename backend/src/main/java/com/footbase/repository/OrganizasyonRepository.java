package com.footbase.repository;

import com.footbase.entity.Organizasyon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Organizasyon repository interface'i
 * Organizasyon veritabanı işlemlerini yönetir
 */
@Repository
public interface OrganizasyonRepository extends JpaRepository<Organizasyon, Long> {

    /**
     * Organizasyon adına göre arama yapar
     * @param ad Organizasyon adı
     * @return Bulunan organizasyon
     */
    Optional<Organizasyon> findByAd(String ad);

    /**
     * Organizasyon adı var mı kontrol eder
     * @param ad Organizasyon adı
     * @return Varsa true, yoksa false
     */
    boolean existsByAd(String ad);

    /**
     * Organizasyon adına göre like araması yapar
     * @param ad Aranacak kelime
     * @return Bulunan organizasyonlar
     */
    @Query("SELECT o FROM Organizasyon o WHERE LOWER(o.ad) LIKE LOWER(CONCAT('%', :ad, '%'))")
    List<Organizasyon> searchByAd(String ad);
}

