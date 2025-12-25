package com.footbase.patterns.builder;

import com.footbase.entity.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Maç Builder Interface (Classic GoF Builder Pattern)
 * 
 * Bu interface, GoF (Gang of Four) Design Patterns kitabında tanımlanan
 * klasik Builder Pattern'in Builder rolünü üstlenir.
 * 
 * BUILDER PATTERN'DEKİ ROLÜ:
 * Builder Interface - Karmaşık nesnenin parçalarını oluşturmak için soyut interface
 * 
 * AMACI:
 * - Farklı builder implementasyonları sağlamak
 * - Aynı build sürecini farklı şekillerde uygulayabilmek
 * - Director ile birlikte kullanılabilir
 * 
 * FARK: Fluent Builder vs Classic Builder
 * - Fluent: Tek sınıf, method chaining, modern
 * - Classic: Interface + Concrete + Director, akademik, esnek
 * 
 * ÖRNEK IMPLEMENTASYONLAR:
 * - StandardMacBuilder: Normal lig maçları için
 * - KupaMacBuilder: Kupa maçları için (gelecekte)
 * - UluslararasiMacBuilder: Uluslararası maçlar için (gelecekte)
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public interface MacBuilderInterface {
    
    /**
     * Takım bilgilerini build eder (zorunlu)
     * @param evSahibi Ev sahibi takım
     * @param deplasman Deplasman takımı
     */
    void buildTakimlar(Takim evSahibi, Takim deplasman);
    
    /**
     * Tarih ve saat bilgilerini build eder (zorunlu)
     * @param tarih Maç tarihi
     * @param saat Maç saati
     */
    void buildTarihSaat(LocalDate tarih, LocalTime saat);
    
    /**
     * Stadyum bilgisini build eder (opsiyonel)
     * @param stadyum Stadyum
     */
    void buildStadyum(Stadyum stadyum);
    
    /**
     * Hakem bilgisini build eder (opsiyonel)
     * @param hakem Hakem
     */
    void buildHakem(Hakem hakem);
    
    /**
     * Lig bilgisini build eder (opsiyonel)
     * @param lig Lig
     */
    void buildLig(Lig lig);
    
    /**
     * Organizasyon bilgisini build eder (opsiyonel)
     * @param organizasyon Organizasyon
     */
    void buildOrganizasyon(Organizasyon organizasyon);
    
    /**
     * Skor bilgilerini build eder (opsiyonel - tamamlanmış maçlar için)
     * @param evSahibiSkor Ev sahibi skor
     * @param deplasmanSkor Deplasman skor
     */
    void buildSkorlar(Integer evSahibiSkor, Integer deplasmanSkor);
    
    /**
     * Maç notu build eder (opsiyonel)
     * @param not Maç notu
     */
    void buildNot(String not);
    
    /**
     * Builder'ı resetler - yeni bir Mac oluşturmaya başlamak için
     */
    void reset();
    
    /**
     * Oluşturulan Mac nesnesini döndürür
     * @return Oluşturulan Mac nesnesi
     */
    Mac getResult();
}

