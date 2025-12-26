package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdminDegerlendirmeStrateji implements DegerlendirmeStrateji {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminDegerlendirmeStrateji.class);
    private static final double AGIRLIK = 3.0;
    
    @Override
    public double puanHesapla(int yildizSayisi) {
        if (yildizSayisi < 1 || yildizSayisi > 5) {
            logger.error("❌ Geçersiz yıldız sayısı: {} (1-5 arası olmalı)", yildizSayisi);
            throw new IllegalArgumentException("Yıldız sayısı 1-5 arasında olmalı!");
        }
        
        double puan = yildizSayisi * AGIRLIK;
        
        logger.info("👑 Admin Değerlendirme: {} yıldız × {} = {} puan", 
                    yildizSayisi, AGIRLIK, puan);
        
        return puan;
    }
    
    @Override
    public double getAgirlik() {
        return AGIRLIK;
    }
    
    @Override
    public String getStratejAdi() {
        return "ADMIN_STRATEJISI";
    }
    
    @Override
    public String getAciklama() {
        return "Admin değerlendirmeleri 3 kat ağırlıklıdır";
    }
    
    @Override
    public String toString() {
        return String.format("AdminDegerlendirmeStrateji{agirlik=%.1fx}", AGIRLIK);
    }
}
