package com.footbase.repository;

import com.footbase.entity.Hakem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Hakem repository interface'i
 * Hakem veritabanı işlemlerini yönetir
 */
@Repository
public interface HakemRepository extends JpaRepository<Hakem, Long> {

    /**
     * Hakem adına göre arama yapar
     * @param adSoyad Hakem adı
     * @return Bulunan hakem
     */
    Optional<Hakem> findByAdSoyad(String adSoyad);

    /**
     * Uyruğa göre hakemleri getirir
     * @param uyruk Uyruk
     * @return Hakem listesi
     */
    List<Hakem> findByUyruk(String uyruk);

    /**
     * Hakem adı var mı kontrol eder
     * @param adSoyad Hakem adı
     * @return Varsa true, yoksa false
     */
    boolean existsByAdSoyad(String adSoyad);

    /**
     * Ad ve soyada göre like araması yapar
     * @param adSoyad Aranacak kelime
     * @return Bulunan hakemler
     */
    @Query("SELECT h FROM Hakem h WHERE LOWER(h.adSoyad) LIKE LOWER(CONCAT('%', :adSoyad, '%'))")
    List<Hakem> searchByAdSoyad(String adSoyad);
}

