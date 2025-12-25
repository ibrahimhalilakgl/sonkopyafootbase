package com.footbase.repository;

import com.footbase.entity.Bildirim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Bildirim Repository
 * 
 * Bu interface, Bildirim entity'si için veritabanı işlemlerini yönetir.
 * Spring Data JPA sayesinde otomatik olarak implementasyon oluşturulur.
 * 
 * SPRING DATA JPA ÖZELLİKLERİ:
 * - Metod isimlendirme ile otomatik sorgu oluşturma
 * - @Query annotation ile custom sorgular
 * - Pagination ve Sorting desteği
 * 
 * OBSERVER PATTERN İLE İLİŞKİSİ:
 * Observer pattern ile oluşturulan bildirimler bu repository
 * aracılığıyla veritabanına kaydedilir ve kullanıcılara sunulur.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Repository
public interface BildirimRepository extends JpaRepository<Bildirim, Long> {
    
    /**
     * Belirli bir kullanıcının tüm bildirimlerini getirir
     * 
     * Spring Data JPA, metod isminden otomatik sorgu oluşturur:
     * SELECT * FROM bildirimler WHERE alici_kullanici_id = ?
     * ORDER BY olusturma_zamani DESC
     * 
     * @param aliciKullaniciId Kullanıcı ID'si
     * @return Bildirimlerin listesi (yeniden eskiye)
     */
    List<Bildirim> findByAliciKullaniciIdOrderByOlusturmaZamaniDesc(Long aliciKullaniciId);
    
    /**
     * Belirli bir kullanıcının okunmamış bildirimlerini getirir
     * 
     * @param aliciKullaniciId Kullanıcı ID'si
     * @param okundu Okunma durumu (false = okunmamış)
     * @return Okunmamış bildirimlerin listesi
     */
    List<Bildirim> findByAliciKullaniciIdAndOkunduOrderByOlusturmaZamaniDesc(
        Long aliciKullaniciId, Boolean okundu);
    
    /**
     * Kullanıcının okunmamış bildirim sayısını döndürür
     * 
     * Bu metod, bildirim badge'inde (rozet) kullanılır.
     * Örnek: Bildirim ikonu üstünde "5" yazısı
     * 
     * @param aliciKullaniciId Kullanıcı ID'si
     * @param okundu Okunma durumu (false = okunmamış)
     * @return Okunmamış bildirim sayısı
     */
    Long countByAliciKullaniciIdAndOkundu(Long aliciKullaniciId, Boolean okundu);
    
    /**
     * Belirli bir maça ait tüm bildirimleri getirir
     * 
     * @param macId Maç ID'si
     * @return Maçla ilgili bildirimlerin listesi
     */
    List<Bildirim> findByMacId(Long macId);
    
    /**
     * Belirli tipteki bildirimleri getirir
     * 
     * @param aliciKullaniciId Kullanıcı ID'si
     * @param bildirimTipi Bildirim tipi (MAC_EKLENDI, YENI_YORUM vb.)
     * @return Belirli tipteki bildirimlerin listesi
     */
    List<Bildirim> findByAliciKullaniciIdAndBildirimTipiOrderByOlusturmaZamaniDesc(
        Long aliciKullaniciId, String bildirimTipi);
    
    /**
     * Bir bildirimi okundu olarak işaretler
     * 
     * @Modifying: Bu sorgu veriyi değiştirir (UPDATE)
     * @Transactional: Transaction içinde çalışır
     * 
     * @param bildirimId Bildirim ID'si
     * @return Güncellenen satır sayısı (1 veya 0)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Bildirim b SET b.okundu = true, b.okunmaZamani = CURRENT_TIMESTAMP WHERE b.id = :bildirimId")
    int okunduOlarakIsaretle(@Param("bildirimId") Long bildirimId);
    
    /**
     * Kullanıcının tüm bildirimlerini okundu olarak işaretler
     * 
     * Toplu güncelleme işlemi. Performanslı çözüm.
     * 
     * @param aliciKullaniciId Kullanıcı ID'si
     * @return Güncellenen satır sayısı
     */
    @Modifying
    @Transactional
    @Query("UPDATE Bildirim b SET b.okundu = true, b.okunmaZamani = CURRENT_TIMESTAMP WHERE b.aliciKullanici.id = :aliciKullaniciId AND b.okundu = false")
    int tumunuOkunduOlarakIsaretle(@Param("aliciKullaniciId") Long aliciKullaniciId);
    
    /**
     * Belirli bir kullanıcının bildirimlerini siler
     * 
     * Genellikle kullanıcı hesabı silinirken kullanılır.
     * Cascade delete ile otomatik yapılabilir ama açık metod daha güvenli.
     * 
     * @param aliciKullaniciId Kullanıcı ID'si
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Bildirim b WHERE b.aliciKullanici.id = :aliciKullaniciId")
    void kullaniciBildirimleriSil(@Param("aliciKullaniciId") Long aliciKullaniciId);
    
    /**
     * Eski okunmuş bildirimleri siler (Temizleme işlemi)
     * 
     * Bu metod, scheduled job ile çalıştırılabilir.
     * Örnek: Her gün 03:00'te 30 günden eski okunmuş bildirimler silinir.
     * 
     * @param gunSayisi Kaç gün önceye kadar silinsin
     * @return Silinen satır sayısı
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM bildirimler WHERE okundu = true AND olusturma_zamani < NOW() - INTERVAL ':gunSayisi days'", 
           nativeQuery = true)
    int eskiBildirimleriSil(@Param("gunSayisi") int gunSayisi);
    
    /**
     * En son N adet bildirimi getirir
     * 
     * Bildirim dropdown'ında son 10 bildirimi göstermek için kullanılabilir.
     * 
     * @param aliciKullaniciId Kullanıcı ID'si
     * @param limit Kaç adet bildirim getirilesin
     * @return Son N bildirimin listesi
     */
    @Query(value = "SELECT * FROM bildirimler WHERE alici_kullanici_id = :aliciKullaniciId ORDER BY olusturma_zamani DESC LIMIT :limit", 
           nativeQuery = true)
    List<Bildirim> sonBildirimleriGetir(@Param("aliciKullaniciId") Long aliciKullaniciId, 
                                        @Param("limit") int limit);
}

