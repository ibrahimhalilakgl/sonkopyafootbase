package com.footbase.patterns.strategy;

/**
 * Değerlendirme Stratejisi (Strategy Interface)
 * 
 * Strategy Pattern'in temel arayüzü.
 * Farklı kullanıcı tiplerinin değerlendirmelerini farklı şekilde hesaplar.
 * 
 * STRATEGY PATTERN:
 * - Algoritmaları bir aile halinde tanımlar
 * - Her birini kapsüller
 * - Birbirinin yerine kullanılabilir yapar
 * 
 * KULLANICI TİPLERİ ve AĞIRLIKLARI:
 * - Admin: 3x ağırlık (en yüksek etki)
 * - Editör: 2x ağırlık (orta etki)
 * - Normal Kullanıcı: 1x ağırlık (standart etki)
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public interface DegerlendirmeStrateji {
    
    /**
     * Değerlendirme puanını hesaplar
     * 
     * @param yildizSayisi Kullanıcının verdiği yıldız sayısı (1-5)
     * @return Ağırlıklandırılmış puan
     */
    double puanHesapla(int yildizSayisi);
    
    /**
     * Stratejinin ağırlık katsayısını döndürür
     * 
     * @return Ağırlık katsayısı
     */
    double getAgirlik();
    
    /**
     * Stratejinin adını döndürür
     * 
     * @return Strateji adı (örn: "ADMIN_STRATEJISI")
     */
    String getStratejAdi();
    
    /**
     * Stratejinin açıklamasını döndürür
     * 
     * @return Açıklama
     */
    String getAciklama();
}

