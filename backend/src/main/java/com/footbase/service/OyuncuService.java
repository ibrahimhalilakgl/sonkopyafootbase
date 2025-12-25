package com.footbase.service;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Oyuncu;
import com.footbase.entity.OyuncuYorumlari;
import com.footbase.entity.Takim;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.OyuncuRepository;
import com.footbase.repository.OyuncuYorumlariRepository;
import com.footbase.repository.TakimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Oyuncu servisi
 * Oyuncu işlemlerini yönetir
 */
@Service
public class OyuncuService {

    @Autowired
    private OyuncuRepository oyuncuRepository;

    @Autowired
    private TakimRepository takimRepository;
    
    @Autowired
    private OyuncuYorumlariRepository oyuncuYorumlariRepository;
    
    @Autowired
    private KullaniciRepository kullaniciRepository;

    /**
     * Tüm oyuncuları getirir
     * @return Oyuncu listesi
     */
    public List<Oyuncu> tumOyunculariGetir() {
        return oyuncuRepository.findAll();
    }

    /**
     * ID'ye göre oyuncu getirir
     * @param id Oyuncu ID'si
     * @return Oyuncu
     * @throws RuntimeException Oyuncu bulunamazsa
     */
    public Oyuncu oyuncuGetir(Long id) {
        return oyuncuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oyuncu bulunamadı"));
    }

    /**
     * ID'ye göre oyuncu detaylarını getirir (ortalama puan ile birlikte)
     * @param id Oyuncu ID'si
     * @return Oyuncu detayları Map formatında
     * @throws RuntimeException Oyuncu bulunamazsa
     */
    public Map<String, Object> oyuncuDetaylariniGetir(Long id) {
        Oyuncu oyuncu = oyuncuGetir(id);
        List<OyuncuYorumlari> yorumlar = oyuncuYorumlariRepository.findByOyuncuIdOrderByOlusturmaTarihiDesc(id);
        
        // Ortalama puanı hesapla
        List<Double> puanlar = new java.util.ArrayList<>();
        for (OyuncuYorumlari yorum : yorumlar) {
            String icerik = yorum.getIcerik();
            if (icerik != null && icerik.startsWith("[") && icerik.contains("/10]")) {
                try {
                    String puanStr = icerik.substring(1, icerik.indexOf("/10]"));
                    double puan = Double.parseDouble(puanStr);
                    puanlar.add(puan);
                } catch (Exception e) {
                    // Hata olursa atla
                }
            }
        }
        
        double ortalamaPuan = 0.0;
        if (!puanlar.isEmpty()) {
            ortalamaPuan = puanlar.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        }
        
        // Map formatında döndür (hem backend hem frontend field adlarını destekle)
        Map<String, Object> detay = new HashMap<>();
        detay.put("id", oyuncu.getId());
        detay.put("ad", oyuncu.getAd());
        detay.put("soyad", oyuncu.getSoyad());
        detay.put("fullName", oyuncu.getAd() + " " + oyuncu.getSoyad()); // Frontend için
        detay.put("pozisyon", oyuncu.getPozisyon());
        detay.put("position", oyuncu.getPozisyon()); // Frontend için
        detay.put("dogumTarihi", oyuncu.getDogumTarihi());
        detay.put("milliyet", oyuncu.getMilliyet());
        detay.put("fotograf", oyuncu.getFotograf());
        detay.put("imageUrl", oyuncu.getFotograf()); // Frontend için
        detay.put("averageRating", ortalamaPuan);
        detay.put("ratingCount", puanlar.size());
        
        // Takım bilgisi varsa ekle
        if (oyuncu.getTakim() != null) {
            Map<String, Object> takimMap = new HashMap<>();
            takimMap.put("id", oyuncu.getTakim().getId());
            takimMap.put("ad", oyuncu.getTakim().getAd());
            takimMap.put("logo", oyuncu.getTakim().getLogo());
            detay.put("takim", takimMap);
            detay.put("team", oyuncu.getTakim().getAd()); // Frontend için
        }
        
        return detay;
    }

    /**
     * Takıma göre oyuncuları getirir
     * @param takimId Takım ID'si
     * @return Oyuncu listesi
     */
    public List<Oyuncu> takimaGoreOyunculariGetir(Long takimId) {
        return oyuncuRepository.findByTakimId(takimId);
    }

    /**
     * Pozisyona göre oyuncuları getirir
     * @param pozisyon Pozisyon
     * @return Oyuncu listesi
     */
    public List<Oyuncu> pozisyonaGoreOyunculariGetir(String pozisyon) {
        return oyuncuRepository.findByPozisyon(pozisyon);
    }

    /**
     * Yeni oyuncu oluşturur
     * @param oyuncu Oyuncu bilgileri
     * @return Oluşturulan oyuncu
     */
    public Oyuncu oyuncuOlustur(Oyuncu oyuncu) {
        // Takımı kontrol et
        if (oyuncu.getTakim() != null && oyuncu.getTakim().getId() != null) {
            Takim takim = takimRepository.findById(oyuncu.getTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Takım bulunamadı"));
            oyuncu.setTakim(takim);
        }

        return oyuncuRepository.save(oyuncu);
    }

    /**
     * Oyuncu bilgilerini günceller
     * @param id Oyuncu ID'si
     * @param oyuncu Güncellenecek oyuncu bilgileri
     * @return Güncellenmiş oyuncu
     */
    public Oyuncu oyuncuGuncelle(Long id, Oyuncu oyuncu) {
        Oyuncu mevcutOyuncu = oyuncuGetir(id);

        if (oyuncu.getAd() != null) {
            mevcutOyuncu.setAd(oyuncu.getAd());
        }
        if (oyuncu.getSoyad() != null) {
            mevcutOyuncu.setSoyad(oyuncu.getSoyad());
        }
        if (oyuncu.getPozisyon() != null) {
            mevcutOyuncu.setPozisyon(oyuncu.getPozisyon());
        }
        // Yaş alanı veritabanında yok, bu yüzden güncellenmiyor
        // if (oyuncu.getYas() != null) {
        //     mevcutOyuncu.setYas(oyuncu.getYas());
        // }
        if (oyuncu.getDogumTarihi() != null) {
            mevcutOyuncu.setDogumTarihi(oyuncu.getDogumTarihi());
        }
        if (oyuncu.getMilliyet() != null) {
            mevcutOyuncu.setMilliyet(oyuncu.getMilliyet());
        }
        if (oyuncu.getFotograf() != null) {
            mevcutOyuncu.setFotograf(oyuncu.getFotograf());
        }
        if (oyuncu.getTakim() != null && oyuncu.getTakim().getId() != null) {
            Takim takim = takimRepository.findById(oyuncu.getTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Takım bulunamadı"));
            mevcutOyuncu.setTakim(takim);
        }

        return oyuncuRepository.save(mevcutOyuncu);
    }

    /**
     * Oyuncuyu siler
     * @param id Oyuncu ID'si
     */
    public void oyuncuSil(Long id) {
        Oyuncu oyuncu = oyuncuGetir(id);
        oyuncuRepository.delete(oyuncu);
    }

    /**
     * Oyuncuya ait yorumları getirir
     * @param oyuncuId Oyuncu ID'si
     * @return Yorum listesi (Map formatında)
     */
    public List<Map<String, Object>> oyuncuYorumlariniGetir(Long oyuncuId) {
        List<OyuncuYorumlari> yorumlar = oyuncuYorumlariRepository.findByOyuncuIdOrderByOlusturmaTarihiDesc(oyuncuId);
        return yorumlar.stream().map(yorum -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", yorum.getId());
            map.put("comment", yorum.getIcerik());
            map.put("icerik", yorum.getIcerik());
            // Yorum içeriğinden puan bilgisini çıkarmaya çalış
            String icerik = yorum.getIcerik();
            if (icerik != null && icerik.startsWith("[") && icerik.contains("/10]")) {
                try {
                    String puanStr = icerik.substring(1, icerik.indexOf("/10]"));
                    map.put("score", Double.parseDouble(puanStr));
                } catch (Exception e) {
                    map.put("score", null);
                }
            } else {
                map.put("score", null);
            }
            map.put("author", yorum.getKullanici() != null ? yorum.getKullanici().getKullaniciAdi() : null);
            map.put("olusturmaTarihi", yorum.getOlusturmaTarihi());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * Oyuncuya yorum ekler
     * @param oyuncuId Oyuncu ID'si
     * @param kullaniciId Kullanıcı ID'si
     * @param score Puan (1-10 arası, opsiyonel)
     * @param comment Yorum içeriği
     * @return Oluşturulan yorum
     */
    public OyuncuYorumlari oyuncuYorumEkle(Long oyuncuId, Long kullaniciId, Integer score, String comment) {
        Oyuncu oyuncu = oyuncuGetir(oyuncuId);
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        // Yorum içeriğini oluştur - puan varsa başa ekle
        String icerik = comment != null && !comment.trim().isEmpty() ? comment.trim() : "";
        if (score != null && score >= 1 && score <= 10) {
            icerik = String.format("[%d/10] %s", score, icerik).trim();
        }
        
        if (icerik.isEmpty()) {
            throw new RuntimeException("Yorum içeriği gereklidir");
        }
        
        OyuncuYorumlari yorum = new OyuncuYorumlari();
        yorum.setOyuncu(oyuncu);
        yorum.setKullanici(kullanici);
        yorum.setIcerik(icerik);
        
        return oyuncuYorumlariRepository.save(yorum);
    }
}


