package com.footbase.repository;

import com.footbase.entity.Mac;
import com.footbase.entity.MacTakimlari;
import com.footbase.entity.Takim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Maç repository interface'i
 * Maç veritabanı işlemlerini yönetir
 */
@Repository
public interface MacRepository extends JpaRepository<Mac, Long> {

    /**
     * Tüm maçları ilişkili entity'lerle birlikte getirir
     * @return Maç listesi
     */
    @Query("SELECT DISTINCT m FROM Mac m " +
           "LEFT JOIN FETCH m.hakem " +
           "LEFT JOIN FETCH m.macTakimlari mt " +
           "LEFT JOIN FETCH mt.takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig")
    List<Mac> findAll();

    /**
     * ID'ye göre maç getirir (ilişkili entity'lerle birlikte)
     * @param id Maç ID'si
     * @return Maç
     */
    @Query("SELECT DISTINCT m FROM Mac m " +
           "LEFT JOIN FETCH m.hakem " +
           "LEFT JOIN FETCH m.macTakimlari mt " +
           "LEFT JOIN FETCH mt.takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "WHERE m.id = :id")
    java.util.Optional<Mac> findById(Long id);

    /**
     * Ev sahibi takıma göre maçları bulur
     * @param takim Ev sahibi takım
     * @return Ev sahibi takımın maçları
     */
    @Query("SELECT DISTINCT m FROM Mac m " +
           "LEFT JOIN FETCH m.hakem " +
           "LEFT JOIN FETCH m.macTakimlari mt " +
           "LEFT JOIN FETCH mt.takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "WHERE EXISTS (SELECT 1 FROM MacTakimlari mt2 WHERE mt2.mac = m AND mt2.takim = :takim AND mt2.evSahibi = true)")
    List<Mac> findByEvSahibiTakim(Takim takim);

    /**
     * Deplasman takıma göre maçları bulur
     * @param takim Deplasman takım
     * @return Deplasman takımın maçları
     */
    @Query("SELECT DISTINCT m FROM Mac m " +
           "LEFT JOIN FETCH m.hakem " +
           "LEFT JOIN FETCH m.macTakimlari mt " +
           "LEFT JOIN FETCH mt.takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "WHERE EXISTS (SELECT 1 FROM MacTakimlari mt2 WHERE mt2.mac = m AND mt2.takim = :takim AND mt2.evSahibi = false)")
    List<Mac> findByDeplasmanTakim(Takim takim);

    // Durum kolonu veritabanında yok, bu metod kullanılamaz
    // List<Mac> findByDurum(String durum);

    /**
     * Tarih aralığına göre maçları bulur
     * @param baslangicTarihi Başlangıç tarihi
     * @param bitisTarihi Bitiş tarihi
     * @return Tarih aralığındaki maçlar
     */
    @Query("SELECT DISTINCT m FROM Mac m " +
           "LEFT JOIN FETCH m.hakem " +
           "LEFT JOIN FETCH m.macTakimlari mt " +
           "LEFT JOIN FETCH mt.takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "WHERE m.tarih BETWEEN :baslangicTarihi AND :bitisTarihi")
    List<Mac> findByTarihBetween(LocalDate baslangicTarihi, LocalDate bitisTarihi);

    /**
     * Gelecek maçları bulur
     * @param simdiTarih Şu anki tarih
     * @param simdiSaat Şu anki saat
     * @return Gelecek maçlar
     */
    @Query("SELECT DISTINCT m FROM Mac m " +
           "LEFT JOIN FETCH m.hakem " +
           "LEFT JOIN FETCH m.macTakimlari mt " +
           "LEFT JOIN FETCH mt.takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "WHERE (m.tarih > :simdiTarih) OR (m.tarih = :simdiTarih AND m.saat > :simdiSaat) " +
           "ORDER BY m.tarih ASC, m.saat ASC")
    List<Mac> findGelecekMaclar(@Param("simdiTarih") java.time.LocalDate simdiTarih, @Param("simdiSaat") java.time.LocalTime simdiSaat);

    /**
     * Geçmiş maçları bulur
     * @param simdiTarih Şu anki tarih
     * @param simdiSaat Şu anki saat
     * @return Geçmiş maçlar
     */
    @Query("SELECT DISTINCT m FROM Mac m " +
           "LEFT JOIN FETCH m.hakem " +
           "LEFT JOIN FETCH m.macTakimlari mt " +
           "LEFT JOIN FETCH mt.takim t " +
           "LEFT JOIN FETCH t.stadyum " +
           "LEFT JOIN FETCH t.lig " +
           "WHERE (m.tarih < :simdiTarih) OR (m.tarih = :simdiTarih AND m.saat < :simdiSaat) " +
           "ORDER BY m.tarih DESC, m.saat DESC")
    List<Mac> findGecmisMaclar(@Param("simdiTarih") java.time.LocalDate simdiTarih, @Param("simdiSaat") java.time.LocalTime simdiSaat);
}

