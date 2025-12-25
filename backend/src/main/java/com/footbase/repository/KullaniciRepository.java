package com.footbase.repository;

import com.footbase.entity.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kullanıcı repository interface'i
 * Kullanıcı veritabanı işlemlerini yönetir
 */
@Repository
public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {

    /**
     * E-posta adresine göre kullanıcı bulur
     * @param email E-posta adresi
     * @return Bulunan kullanıcı
     */
    Optional<Kullanici> findByEmail(String email);

    /**
     * Kullanıcı adına göre kullanıcı bulur
     * @param kullaniciAdi Kullanıcı adı
     * @return Bulunan kullanıcı
     */
    Optional<Kullanici> findByKullaniciAdi(String kullaniciAdi);

    /**
     * E-posta veya kullanıcı adına göre kullanıcı bulur
     * @param email E-posta adresi
     * @param kullaniciAdi Kullanıcı adı
     * @return Bulunan kullanıcı
     */
    Optional<Kullanici> findByEmailOrKullaniciAdi(String email, String kullaniciAdi);

    /**
     * E-posta adresinin var olup olmadığını kontrol eder
     * @param email E-posta adresi
     * @return E-posta varsa true, yoksa false
     */
    boolean existsByEmail(String email);

    /**
     * Kullanıcı adının var olup olmadığını kontrol eder
     * @param kullaniciAdi Kullanıcı adı
     * @return Kullanıcı adı varsa true, yoksa false
     */
    boolean existsByKullaniciAdi(String kullaniciAdi);
}



