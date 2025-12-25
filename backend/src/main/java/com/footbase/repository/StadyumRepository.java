package com.footbase.repository;

import com.footbase.entity.Stadyum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Stadyum repository interface'i
 * Stadyum veritabanı işlemlerini yönetir
 */
@Repository
public interface StadyumRepository extends JpaRepository<Stadyum, Long> {

    /**
     * Stadyum adına göre arama yapar
     * @param stadyumAdi Stadyum adı
     * @return Bulunan stadyum
     */
    Optional<Stadyum> findByStadyumAdi(String stadyumAdi);

    /**
     * Şehre göre stadyumları getirir
     * @param sehir Şehir
     * @return Stadyum listesi
     */
    List<Stadyum> findBySehir(String sehir);

    /**
     * Ülkeye göre stadyumları getirir
     * @param ulke Ülke
     * @return Stadyum listesi
     */
    List<Stadyum> findByUlke(String ulke);

    /**
     * Stadyum adı var mı kontrol eder
     * @param stadyumAdi Stadyum adı
     * @return Varsa true, yoksa false
     */
    boolean existsByStadyumAdi(String stadyumAdi);

    /**
     * Stadyum adına göre like araması yapar
     * @param stadyumAdi Aranacak kelime
     * @return Bulunan stadyumlar
     */
    @Query("SELECT s FROM Stadyum s WHERE LOWER(s.stadyumAdi) LIKE LOWER(CONCAT('%', :stadyumAdi, '%'))")
    List<Stadyum> searchByStadyumAdi(String stadyumAdi);
}

