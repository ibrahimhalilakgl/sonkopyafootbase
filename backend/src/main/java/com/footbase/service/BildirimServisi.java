package com.footbase.service;

import com.footbase.entity.Bildirim;
import com.footbase.repository.BildirimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Bildirim Servisi
 * 
 * Bildirim işlemlerini yöneten servis katmanı.
 * Observer Pattern ile oluşturulan bildirimlerin yönetimi burada yapılır.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Service
public class BildirimServisi {
    
    @Autowired
    private BildirimRepository bildirimRepository;
    
    /**
     * Kullanıcının tüm bildirimlerini getirir
     */
    public List<Bildirim> kullaniciBildirimleriGetir(Long kullaniciId) {
        return bildirimRepository.findByAliciKullaniciIdOrderByOlusturmaZamaniDesc(kullaniciId);
    }
    
    /**
     * Okunmamış bildirimleri getirir
     */
    public List<Bildirim> okunmamisBildirimleriGetir(Long kullaniciId) {
        return bildirimRepository.findByAliciKullaniciIdAndOkunduOrderByOlusturmaZamaniDesc(
            kullaniciId, false);
    }
    
    /**
     * Okunmamış bildirim sayısını döndürür
     */
    public Long okunmamisBildirimSayisi(Long kullaniciId) {
        return bildirimRepository.countByAliciKullaniciIdAndOkundu(kullaniciId, false);
    }
    
    /**
     * Son N bildirimi getirir
     */
    public List<Bildirim> sonBildirimleriGetir(Long kullaniciId, int limit) {
        return bildirimRepository.sonBildirimleriGetir(kullaniciId, limit);
    }
    
    /**
     * Bildirimi okundu olarak işaretler
     */
    @Transactional
    public void bildirimOkunduIsaretle(Long bildirimId) {
        bildirimRepository.okunduOlarakIsaretle(bildirimId);
    }
    
    /**
     * Tüm bildirimleri okundu işaretle
     */
    @Transactional
    public int tumBildirimleriOkunduIsaretle(Long kullaniciId) {
        return bildirimRepository.tumunuOkunduOlarakIsaretle(kullaniciId);
    }
    
    /**
     * Bildirimi siler
     */
    @Transactional
    public void bildirimSil(Long bildirimId) {
        bildirimRepository.deleteById(bildirimId);
    }
}

