package com.footbase.repository;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.entity.Yorum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Yorum repository interface'i
 * Yorum veritabanı işlemlerini yönetir
 */
@Repository
public interface YorumRepository extends JpaRepository<Yorum, Long> {

    /**
     * Maça göre yorumları bulur
     * @param mac Maç
     * @return Maça ait yorumlar listesi
     */
    List<Yorum> findByMac(Mac mac);

    /**
     * Maç ID'sine göre yorumları bulur
     * @param macId Maç ID'si
     * @return Maça ait yorumlar listesi
     */
    List<Yorum> findByMacId(Long macId);

    /**
     * Maça göre yorumları tarihe göre sıralı bulur
     * @param mac Maç
     * @return Maça ait yorumlar listesi (tarihe göre sıralı)
     */
    @Query("SELECT DISTINCT y FROM Yorum y " +
           "LEFT JOIN FETCH y.kullanici " +
           "WHERE y.mac = :mac " +
           "ORDER BY y.yorumTarihi DESC")
    List<Yorum> findByMacOrderByYorumTarihiDesc(Mac mac);
    
    /**
     * En son yorumları getirir (tarihe göre azalan sırada)
     * @param pageable Sayfalama bilgisi (limit için)
     * @return En son yorumlar listesi
     */
    @Query("SELECT DISTINCT y FROM Yorum y " +
           "LEFT JOIN FETCH y.kullanici " +
           "LEFT JOIN FETCH y.mac " +
           "ORDER BY y.yorumTarihi DESC")
    List<Yorum> findTopByOrderByYorumTarihiDesc(Pageable pageable);

    /**
     * Kullanıcıya göre yorumları bulur (tarihe göre sıralı)
     * @param kullanici Kullanıcı
     * @return Kullanıcıya ait yorumlar listesi
     */
    @Query("SELECT DISTINCT y FROM Yorum y " +
           "LEFT JOIN FETCH y.mac m " +
           "WHERE y.kullanici = :kullanici " +
           "ORDER BY y.yorumTarihi DESC")
    List<Yorum> findByKullaniciOrderByYorumTarihiDesc(Kullanici kullanici);
}


