package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NormalKullaniciDegerlendirmeStrateji implements DegerlendirmeStrateji {
    
    private static final Logger logger = LoggerFactory.getLogger(NormalKullaniciDegerlendirmeStrateji.class);
    private static final double AGIRLIK = 1.0;
    
    @Override
    public double puanHesapla(int yildizSayisi) {
        if (yildizSayisi < 1 || yildizSayisi > 5) {
            logger.error("❌ Geçersiz yıldız sayısı: {} (1-5 arası olmalı)", yildizSayisi);
            throw new IllegalArgumentException("Yıldız sayısı 1-5 arasında olmalı!");
        }
        
        double puan = yildizSayisi * AGIRLIK;
        
        logger.info("👤 Normal Kullanıcı Değerlendirme: {} yıldız × {} = {} puan", 
                    yildizSayisi, AGIRLIK, puan);
        
        return puan;
    }
    
    @Override
    public double getAgirlik() {
        return AGIRLIK;
    }
    
    @Override
    public String getStratejAdi() {
        return "NORMAL_KULLANICI_STRATEJISI";
    }
    
    @Override
    public String getAciklama() {
        return "Normal kullanıcı değerlendirmeleri 1 kat ağırlıklıdır";
    }
    
    @Override
    public String toString() {
        return String.format("NormalKullaniciDegerlendirmeStrateji{agirlik=%.1fx}", AGIRLIK);
    }
}
