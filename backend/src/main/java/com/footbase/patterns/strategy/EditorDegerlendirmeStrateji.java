package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EditorDegerlendirmeStrateji implements DegerlendirmeStrateji {
    
    private static final Logger logger = LoggerFactory.getLogger(EditorDegerlendirmeStrateji.class);
    private static final double AGIRLIK = 2.0;
    
    @Override
    public double puanHesapla(int yildizSayisi) {
        if (yildizSayisi < 1 || yildizSayisi > 5) {
            logger.error("❌ Geçersiz yıldız sayısı: {} (1-5 arası olmalı)", yildizSayisi);
            throw new IllegalArgumentException("Yıldız sayısı 1-5 arasında olmalı!");
        }
        
        double puan = yildizSayisi * AGIRLIK;
        
        logger.info("✏️ Editör Değerlendirme: {} yıldız × {} = {} puan", 
                    yildizSayisi, AGIRLIK, puan);
        
        return puan;
    }
    
    @Override
    public double getAgirlik() {
        return AGIRLIK;
    }
    
    @Override
    public String getStratejAdi() {
        return "EDITOR_STRATEJISI";
    }
    
    @Override
    public String getAciklama() {
        return "Editör değerlendirmeleri 2 kat ağırlıklıdır";
    }
    
    @Override
    public String toString() {
        return String.format("EditorDegerlendirmeStrateji{agirlik=%.1fx}", AGIRLIK);
    }
}
