package com.footbase.patterns.builder;

import com.footbase.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Maç Director (Classic GoF Builder Pattern)
 * 
 * Bu sınıf, Builder Pattern'deki Director rolünü üstlenir.
 * Builder'ı kullanarak kompleks Mac nesnelerini belirli senaryolara göre oluşturur.
 * 
 * BUILDER PATTERN'DEKİ ROLÜ:
 * Director - Builder'ı kullanarak belirli konfigürasyonlarda ürünler oluşturan sınıf
 * 
 * AMACI:
 * - Kompleks build senaryolarını merkezi hale getirmek
 * - Farklı Mac türlerini (Lig, Kupa, Uluslararası) standart şekilde oluşturmak
 * - İş mantığını Builder'dan ayırmak
 * 
 * AVANTAJLARI:
 * 1. İş mantığı (Director) ile build süreci (Builder) ayrıldı
 * 2. Yeni Mac türleri eklemek kolay (yeni method ekle)
 * 3. Client kod daha basit (tek method çağrısı)
 * 4. Test edilebilir (mock builder kullanılabilir)
 * 
 * KULLANIM:
 * ```java
 * MacBuilderInterface builder = new StandardMacBuilder();
 * MacDirector director = new MacDirector(builder);
 * 
 * // Hızlı lig maçı
 * Mac ligMaci = director.yaratLigMaci(galatasaray, fenerbahce, tarih, saat);
 * 
 * // Detaylı lig maçı
 * Mac detayliMac = director.yaratDetayliLigMaci(
 *     galatasaray, fenerbahce, tarih, saat, 
 *     ttArena, hakem, superLig
 * );
 * 
 * // Tamamlanmış maç
 * Mac tamamlanmisMac = director.yaratTamamlanmisMac(
 *     galatasaray, fenerbahce, tarih, saat, 2, 1
 * );
 * ```
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public class MacDirector {
    
    private static final Logger logger = LoggerFactory.getLogger(MacDirector.class);
    
    // Builder instance
    private MacBuilderInterface builder;
    
    /**
     * Constructor
     * @param builder Kullanılacak builder instance
     */
    public MacDirector(MacBuilderInterface builder) {
        this.builder = builder;
        logger.info("🎬 MacDirector oluşturuldu (Classic GoF Pattern)");
    }
    
    /**
     * Builder'ı değiştirir (farklı builder kullanmak için)
     * @param builder Yeni builder instance
     */
    public void setBuilder(MacBuilderInterface builder) {
        this.builder = builder;
        logger.debug("🔄 Builder değiştirildi");
    }
    
    /**
     * Basit lig maçı oluşturur (sadece zorunlu alanlar)
     * 
     * @param evSahibi Ev sahibi takım
     * @param deplasman Deplasman takımı
     * @param tarih Maç tarihi
     * @param saat Maç saati
     * @return Oluşturulan Mac
     */
    public Mac yaratLigMaci(Takim evSahibi, Takim deplasman, LocalDate tarih, LocalTime saat) {
        logger.info("🎬 Director: Basit lig maçı oluşturuluyor...");
        
        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        
        Mac mac = builder.getResult();
        logger.info("✅ Basit lig maçı oluşturuldu");
        return mac;
    }
    
    /**
     * Detaylı lig maçı oluşturur (stadyum, hakem, lig bilgileriyle)
     * 
     * @param evSahibi Ev sahibi takım
     * @param deplasman Deplasman takımı
     * @param tarih Maç tarihi
     * @param saat Maç saati
     * @param stadyum Stadyum
     * @param hakem Hakem
     * @param lig Lig
     * @return Oluşturulan Mac
     */
    public Mac yaratDetayliLigMaci(
            Takim evSahibi, 
            Takim deplasman, 
            LocalDate tarih, 
            LocalTime saat,
            Stadyum stadyum,
            Hakem hakem,
            Lig lig) {
        
        logger.info("🎬 Director: Detaylı lig maçı oluşturuluyor...");
        
        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildStadyum(stadyum);
        builder.buildHakem(hakem);
        builder.buildLig(lig);
        
        Mac mac = builder.getResult();
        logger.info("✅ Detaylı lig maçı oluşturuldu");
        return mac;
    }
    
    /**
     * Tam kapsamlı lig maçı oluşturur (tüm detaylarla)
     * 
     * @param evSahibi Ev sahibi takım
     * @param deplasman Deplasman takımı
     * @param tarih Maç tarihi
     * @param saat Maç saati
     * @param stadyum Stadyum
     * @param hakem Hakem
     * @param lig Lig
     * @param organizasyon Organizasyon
     * @param not Maç notu
     * @return Oluşturulan Mac
     */
    public Mac yaratTamKapsamliMac(
            Takim evSahibi, 
            Takim deplasman, 
            LocalDate tarih, 
            LocalTime saat,
            Stadyum stadyum,
            Hakem hakem,
            Lig lig,
            Organizasyon organizasyon,
            String not) {
        
        logger.info("🎬 Director: Tam kapsamlı maç oluşturuluyor...");
        
        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildStadyum(stadyum);
        builder.buildHakem(hakem);
        builder.buildLig(lig);
        builder.buildOrganizasyon(organizasyon);
        builder.buildNot(not);
        
        Mac mac = builder.getResult();
        logger.info("✅ Tam kapsamlı maç oluşturuldu");
        return mac;
    }
    
    /**
     * Tamamlanmış maç oluşturur (skorlarla)
     * 
     * @param evSahibi Ev sahibi takım
     * @param deplasman Deplasman takımı
     * @param tarih Maç tarihi
     * @param saat Maç saati
     * @param evSahibiSkor Ev sahibi skor
     * @param deplasmanSkor Deplasman skor
     * @return Oluşturulan Mac
     */
    public Mac yaratTamamlanmisMac(
            Takim evSahibi, 
            Takim deplasman, 
            LocalDate tarih, 
            LocalTime saat,
            Integer evSahibiSkor,
            Integer deplasmanSkor) {
        
        logger.info("🎬 Director: Tamamlanmış maç oluşturuluyor...");
        
        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildSkorlar(evSahibiSkor, deplasmanSkor);
        
        Mac mac = builder.getResult();
        logger.info("✅ Tamamlanmış maç oluşturuldu: {} - {}", evSahibiSkor, deplasmanSkor);
        return mac;
    }
    
    /**
     * Derbi maçı oluşturur (özel notla)
     * 
     * @param evSahibi Ev sahibi takım
     * @param deplasman Deplasman takımı
     * @param tarih Maç tarihi
     * @param saat Maç saati
     * @param stadyum Stadyum
     * @param hakem Hakem
     * @return Oluşturulan Mac
     */
    public Mac yaratDerbiMaci(
            Takim evSahibi, 
            Takim deplasman, 
            LocalDate tarih, 
            LocalTime saat,
            Stadyum stadyum,
            Hakem hakem) {
        
        logger.info("🎬 Director: Derbi maçı oluşturuluyor...");
        
        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildStadyum(stadyum);
        builder.buildHakem(hakem);
        builder.buildNot("⚡ DERBİ MAÇI - Yüksek güvenlik tedbirleri");
        
        Mac mac = builder.getResult();
        logger.info("✅ Derbi maçı oluşturuldu");
        return mac;
    }
    
    /**
     * Şampiyonluk maçı oluşturur (özel notla)
     * 
     * @param evSahibi Ev sahibi takım
     * @param deplasman Deplasman takımı
     * @param tarih Maç tarihi
     * @param saat Maç saati
     * @param stadyum Stadyum
     * @param hakem Hakem
     * @param lig Lig
     * @return Oluşturulan Mac
     */
    public Mac yaratSampiyonlukMaci(
            Takim evSahibi, 
            Takim deplasman, 
            LocalDate tarih, 
            LocalTime saat,
            Stadyum stadyum,
            Hakem hakem,
            Lig lig) {
        
        logger.info("🎬 Director: Şampiyonluk maçı oluşturuluyor...");
        
        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildStadyum(stadyum);
        builder.buildHakem(hakem);
        builder.buildLig(lig);
        builder.buildNot("🏆 ŞAMPİYONLUK MAÇI - Kritik öneme sahip");
        
        Mac mac = builder.getResult();
        logger.info("✅ Şampiyonluk maçı oluşturuldu");
        return mac;
    }
    
    /**
     * Test maçı oluşturur
     * 
     * @param evSahibi Ev sahibi takım
     * @param deplasman Deplasman takımı
     * @param tarih Maç tarihi
     * @param saat Maç saati
     * @return Oluşturulan Mac
     */
    public Mac yaratTestMaci(
            Takim evSahibi, 
            Takim deplasman, 
            LocalDate tarih, 
            LocalTime saat) {
        
        logger.info("🎬 Director: Test maçı oluşturuluyor...");
        
        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildNot("🧪 TEST MAÇI - Resmi maç değil");
        
        Mac mac = builder.getResult();
        logger.info("✅ Test maçı oluşturuldu");
        return mac;
    }
}

