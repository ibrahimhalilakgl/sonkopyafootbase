package com.footbase.service;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.entity.Yorum;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.MacRepository;
import com.footbase.repository.YorumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Yorum servisi
 * Yorum işlemlerini yönetir
 */
@Service
public class YorumService {

    @Autowired
    private YorumRepository yorumRepository;

    @Autowired
    private MacRepository macRepository;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    /**
     * Maça ait yorumları getirir
     * @param macId Maç ID'si
     * @return Yorum listesi
     */
    public List<Yorum> macYorumlariniGetir(Long macId) {
        return yorumRepository.findByMacOrderByYorumTarihiDesc(
                macRepository.findById(macId)
                        .orElseThrow(() -> new RuntimeException("Maç bulunamadı"))
        );
    }

    /**
     * Yeni yorum oluşturur
     * @param macId Maç ID'si
     * @param kullaniciId Kullanıcı ID'si
     * @param mesaj Yorum mesajı
     * @return Oluşturulan yorum
     */
    public Yorum yorumOlustur(Long macId, Long kullaniciId, String mesaj) {
        Mac mac = macRepository.findById(macId)
                .orElseThrow(() -> new RuntimeException("Maç bulunamadı"));
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Yorum yorum = new Yorum();
        yorum.setMac(mac);
        yorum.setKullanici(kullanici);
        yorum.setMesaj(mesaj);

        return yorumRepository.save(yorum);
    }

    /**
     * Yorum bilgilerini günceller
     * @param yorumId Yorum ID'si
     * @param kullaniciId Kullanıcı ID'si
     * @param mesaj Yeni yorum mesajı
     * @return Güncellenmiş yorum
     * @throws RuntimeException Yorum bulunamazsa veya kullanıcı yetkili değilse
     */
    public Yorum yorumGuncelle(Long yorumId, Long kullaniciId, String mesaj) {
        Yorum yorum = yorumRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadı"));

        // Yorum sahibi kontrolü
        if (!yorum.getKullanici().getId().equals(kullaniciId)) {
            throw new RuntimeException("Bu yorumu güncelleme yetkiniz yok");
        }

        yorum.setMesaj(mesaj);
        return yorumRepository.save(yorum);
    }

    /**
     * Yorumu siler
     * @param yorumId Yorum ID'si
     * @param kullaniciId Kullanıcı ID'si
     * @throws RuntimeException Yorum bulunamazsa veya kullanıcı yetkili değilse
     */
    public void yorumSil(Long yorumId, Long kullaniciId) {
        Yorum yorum = yorumRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadı"));

        // Yorum sahibi kontrolü
        if (!yorum.getKullanici().getId().equals(kullaniciId)) {
            throw new RuntimeException("Bu yorumu silme yetkiniz yok");
        }

        yorumRepository.delete(yorum);
    }

    /**
     * Yorumu beğenir veya beğeniyi kaldırır
     * @param yorumId Yorum ID'si
     * @param kullaniciId Kullanıcı ID'si
     * @return Beğeni durumu (beğenildi mi?)
     */
    public boolean yorumBegen(Long yorumId, Long kullaniciId) {
        Yorum yorum = yorumRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadı"));
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Beğeni durumunu kontrol et
        boolean begenildi = yorum.getBegenenKullanicilar().contains(kullanici);

        if (begenildi) {
            // Beğeniyi kaldır
            yorum.getBegenenKullanicilar().remove(kullanici);
        } else {
            // Beğen
            yorum.getBegenenKullanicilar().add(kullanici);
        }

        yorumRepository.save(yorum);
        return !begenildi; // Yeni durum
    }

    /**
     * En son yorumları getirir
     * @param limit Kaç yorum getirileceği
     * @return En son yorumlar listesi
     */
    public List<Yorum> sonYorumlariGetir(int limit) {
        return yorumRepository.findTopByOrderByYorumTarihiDesc(PageRequest.of(0, limit));
    }
}


