package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Strategy Pattern Kullanım Örneği
 * 
 * Bu sınıf, Strategy Pattern'in nasıl kullanıldığını gösterir.
 * Uygulama başladığında otomatik çalışır ve örnekleri konsola yazdırır.
 * 
 * @Component: Spring bileşeni olarak işaretlendi (otomatik yüklenecek)
 * @Order(100): Diğer pattern'lerden sonra çalışsın (opsiyonel)
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class StrategyPatternOrnek implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(StrategyPatternOrnek.class);
    
    @Autowired
    private DegerlendirmeContext context;
    
    @Autowired
    private DegerlendirmeStratejiFactory factory;
    
    @Override
    public void run(String... args) {
        // Sadece Strategy Pattern aktifse çalıştır
        if (Boolean.parseBoolean(System.getProperty("patterns.strategy.example", "false"))) {
            ornekleriGoster();
        }
    }
    
    /**
     * Strategy Pattern örneklerini gösterir
     */
    public void ornekleriGoster() {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("🎯 STRATEGY PATTERN KULLANIM ÖRNEKLERİ");
        logger.info("═══════════════════════════════════════════════════════");
        
        ornek1_TemelKullanim();
        ornek2_DirekHesaplama();
        ornek3_CokluDegerlendirme();
        ornek4_MacSenaryo();
        
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("✅ Strategy Pattern örnekleri tamamlandı!");
        logger.info("═══════════════════════════════════════════════════════\n");
    }
    
    /**
     * Örnek 1: Temel Kullanım - Strateji seçimi ve hesaplama
     */
    private void ornek1_TemelKullanim() {
        logger.info("\n📌 ÖRNEK 1: Temel Kullanım");
        logger.info("─────────────────────────────────────────────────────");
        
        // Admin değerlendirmesi
        context.stratejiSec("ADMIN");
        double adminPuan = context.puanHesapla(5);
        logger.info("Sonuç: {} puan\n", adminPuan);
        
        // Editör değerlendirmesi
        context.stratejiSec("EDITOR");
        double editorPuan = context.puanHesapla(5);
        logger.info("Sonuç: {} puan\n", editorPuan);
        
        // Normal kullanıcı değerlendirmesi
        context.stratejiSec("USER");
        double normalPuan = context.puanHesapla(5);
        logger.info("Sonuç: {} puan", normalPuan);
    }
    
    /**
     * Örnek 2: Direkt Hesaplama - Tek satırda
     */
    private void ornek2_DirekHesaplama() {
        logger.info("\n📌 ÖRNEK 2: Direkt Hesaplama");
        logger.info("─────────────────────────────────────────────────────");
        
        double puan1 = context.hesapla("ADMIN", 4);
        logger.info("Admin 4 yıldız = {} puan", puan1);
        
        double puan2 = context.hesapla("EDITOR", 3);
        logger.info("Editör 3 yıldız = {} puan", puan2);
        
        double puan3 = context.hesapla("USER", 5);
        logger.info("Normal 5 yıldız = {} puan", puan3);
    }
    
    /**
     * Örnek 3: Çoklu Değerlendirme - Toplam ve ortalama
     */
    private void ornek3_CokluDegerlendirme() {
        logger.info("\n📌 ÖRNEK 3: Çoklu Değerlendirme");
        logger.info("─────────────────────────────────────────────────────");
        
        List<DegerlendirmeContext.Degerlendirme> degerlendirmeler = Arrays.asList(
            new DegerlendirmeContext.Degerlendirme("ADMIN", 5),    // 15 puan
            new DegerlendirmeContext.Degerlendirme("ADMIN", 4),    // 12 puan
            new DegerlendirmeContext.Degerlendirme("EDITOR", 5),   // 10 puan
            new DegerlendirmeContext.Degerlendirme("EDITOR", 3),   // 6 puan
            new DegerlendirmeContext.Degerlendirme("USER", 5),     // 5 puan
            new DegerlendirmeContext.Degerlendirme("USER", 4),     // 4 puan
            new DegerlendirmeContext.Degerlendirme("USER", 3)      // 3 puan
        );
        
        double toplamPuan = context.toplamPuanHesapla(degerlendirmeler);
        double ortalama = context.ortalamaPuanHesapla(degerlendirmeler);
        
        logger.info("\n💯 Toplam Puan: {}", toplamPuan);
        logger.info("⭐ Ortalama: {}/5.0", String.format("%.2f", ortalama));
    }
    
    /**
     * Örnek 4: Gerçek Maç Senaryosu
     */
    private void ornek4_MacSenaryo() {
        logger.info("\n📌 ÖRNEK 4: Gerçek Maç Senaryosu");
        logger.info("─────────────────────────────────────────────────────");
        logger.info("Maç: Galatasaray vs Fenerbahçe");
        logger.info("");
        
        // Değerlendirmeler
        List<DegerlendirmeContext.Degerlendirme> macDegerlendirmeleri = Arrays.asList(
            // Admin değerlendirmeleri
            new DegerlendirmeContext.Degerlendirme("ADMIN", 5),
            new DegerlendirmeContext.Degerlendirme("ADMIN", 5),
            
            // Editör değerlendirmeleri
            new DegerlendirmeContext.Degerlendirme("EDITOR", 4),
            new DegerlendirmeContext.Degerlendirme("EDITOR", 5),
            new DegerlendirmeContext.Degerlendirme("EDITOR", 4),
            
            // Normal kullanıcı değerlendirmeleri
            new DegerlendirmeContext.Degerlendirme("USER", 5),
            new DegerlendirmeContext.Degerlendirme("USER", 4),
            new DegerlendirmeContext.Degerlendirme("USER", 5),
            new DegerlendirmeContext.Degerlendirme("USER", 3),
            new DegerlendirmeContext.Degerlendirme("USER", 4)
        );
        
        double toplam = context.toplamPuanHesapla(macDegerlendirmeleri);
        double ortalama = context.ortalamaPuanHesapla(macDegerlendirmeleri);
        
        logger.info("\n🏆 Maç Değerlendirme Sonucu:");
        logger.info("   💯 Toplam Puan: {}", toplam);
        logger.info("   ⭐ Ortalama: {}/5.0", String.format("%.2f", ortalama));
        logger.info("   🎖️ Sınıflandırma: {}", siniflandir(ortalama));
    }
    
    /**
     * Puanı sınıflandırır
     */
    private String siniflandir(double ortalama) {
        if (ortalama >= 4.5) return "Mükemmel ⭐⭐⭐⭐⭐";
        if (ortalama >= 3.5) return "Çok İyi ⭐⭐⭐⭐";
        if (ortalama >= 2.5) return "İyi ⭐⭐⭐";
        if (ortalama >= 1.5) return "Orta ⭐⭐";
        return "Zayıf ⭐";
    }
    
    /**
     * Factory kullanım örneği
     */
    @SuppressWarnings("unused")
    private void ornekFactory() {
        logger.info("\n📌 Factory ile Kullanım");
        logger.info("─────────────────────────────────────────────────────");
        
        // Factory'den strateji al
        DegerlendirmeStrateji strateji = factory.getStrateji("ADMIN");
        
        // Strateji bilgilerini göster
        logger.info("Strateji: {}", strateji.getStratejAdi());
        logger.info("Ağırlık: {}x", strateji.getAgirlik());
        logger.info("Açıklama: {}", strateji.getAciklama());
        
        // Hesaplama yap
        double puan = strateji.puanHesapla(5);
        logger.info("Puan: {}", puan);
    }
}

