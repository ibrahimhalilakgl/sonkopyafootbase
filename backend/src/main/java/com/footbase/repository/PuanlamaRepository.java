package com.footbase.repository;

import com.footbase.entity.Mac;
import com.footbase.entity.Puanlama;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Puanlama repository interface'i
 * Maç puanlama veritabanı işlemlerini yönetir
 * Not: Veritabanında maça puanlama yapılıyor, oyuncuya değil
 */
@Repository
public interface PuanlamaRepository extends JpaRepository<Puanlama, Long> {

    /**
     * Maça göre puanlamaları bulur
     * @param mac Maç
     * @return Maça ait puanlamalar listesi
     */
    List<Puanlama> findByMac(Mac mac);

    /**
     * Maç ID'sine göre puanlamaları bulur
     * @param macId Maç ID'si
     * @return Maça ait puanlamalar listesi
     */
    List<Puanlama> findByMacId(Long macId);

    /**
     * Kullanıcı ve maça göre puanlamayı bulur
     * @param kullaniciId Kullanıcı ID'si
     * @param macId Maç ID'si
     * @return Bulunan puanlama
     */
    Optional<Puanlama> findByKullaniciIdAndMacId(Long kullaniciId, Long macId);

    /**
     * Maçın ortalama puanını hesaplar
     * @param macId Maç ID'si
     * @return Ortalama puan
     */
    @Query("SELECT AVG(p.puan) FROM Puanlama p WHERE p.mac.id = :macId")
    Double findOrtalamaPuanByMacId(@Param("macId") Long macId);

    /**
     * Maça yapılan puanlama sayısını bulur
     * @param macId Maç ID'si
     * @return Puanlama sayısı
     */
    long countByMacId(Long macId);
}

