package com.footbase.repository;

import com.footbase.entity.TeknikDirektor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Teknik Direktör repository interface'i
 * Teknik direktör veritabanı işlemlerini yönetir
 */
@Repository
public interface TeknikDirektorRepository extends JpaRepository<TeknikDirektor, Long> {

    /**
     * Teknik direktör adına göre arama yapar
     * @param adSoyad Teknik direktör adı
     * @return Bulunan teknik direktör
     */
    Optional<TeknikDirektor> findByAdSoyad(String adSoyad);

    /**
     * Uyruğa göre teknik direktörleri getirir
     * @param uyruk Uyruk
     * @return Teknik direktör listesi
     */
    List<TeknikDirektor> findByUyruk(String uyruk);

    /**
     * Teknik direktör adı var mı kontrol eder
     * @param adSoyad Teknik direktör adı
     * @return Varsa true, yoksa false
     */
    boolean existsByAdSoyad(String adSoyad);

    /**
     * Ad ve soyada göre like araması yapar
     * @param adSoyad Aranacak kelime
     * @return Bulunan teknik direktörler
     */
    @Query("SELECT td FROM TeknikDirektor td WHERE LOWER(td.adSoyad) LIKE LOWER(CONCAT('%', :adSoyad, '%'))")
    List<TeknikDirektor> searchByAdSoyad(String adSoyad);
}

