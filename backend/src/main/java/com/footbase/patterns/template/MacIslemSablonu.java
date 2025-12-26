package com.footbase.patterns.template;

import com.footbase.entity.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maç İşlem Şablonu (Template Method Pattern)
 * 
 * Bu abstract sınıf, maç işleme algoritmasının iskeletini tanımlar.
 * Alt sınıflar belirli adımları override ederek özelleştirme yapar.
 * 
 * TEMPLATE METHOD PATTERN:
 * - İskeleti değiştirilemez (final)
 * - Adımları alt sınıflar özelleştirir
 * - Kod tekrarını önler
 * - Tutarlı işlem garantisi
 * 
 * İŞLEM ADIMLARI:
 * 1. Ön kontroller
 * 2. Verileri doğrula
 * 3. Maç işle
 * 4. Kaydet
 * 5. Bildirim gönder
 * 6. Son işlemler
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public abstract class MacIslemSablonu {
    
    protected static final Logger logger = LoggerFactory.getLogger(MacIslemSablonu.class);
    
    /**
     * TEMPLATE METHOD - İşlem akışının ana iskeleti
     * 
     * Bu metod FINAL'dir - alt sınıflar değiştiremez!
     * İşlem sırasını garanti eder.
     * 
     * @param mac İşlenecek maç
     * @return İşlem başarılı mı?
     */
    public final boolean macIsle(Mac mac) {
        logger.info("🎯 Maç işleme başlatılıyor... [{}]", this.getClass().getSimpleName());
        
        try {
            // 1. Ön kontroller
            if (!onKontrollerYap(mac)) {
                logger.error("❌ Ön kontroller başarısız!");
                return false;
            }
            
            // 2. Verileri doğrula
            if (!verileriDogrula(mac)) {
                logger.error("❌ Veri doğrulama başarısız!");
                return false;
            }
            
            // 3. Maç işle (alt sınıf implementasyonu)
            logger.info("⚙️ Maç işleniyor...");
            maciIsle(mac);
            
            // 4. Kaydet
            logger.info("💾 Maç kaydediliyor...");
            kaydet(mac);
            
            // 5. Bildirim gönder (opsiyonel - hook method)
            if (bildirimGonder()) {
                logger.info("📧 Bildirimler gönderiliyor...");
                bildirimGonderImpl(mac);
            }
            
            // 6. Son işlemler (opsiyonel - hook method)
            sonIslemler(mac);
            
            logger.info("✅ Maç işleme tamamlandı!");
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Maç işleme hatası: {}", e.getMessage(), e);
            hataYonet(mac, e);
            return false;
        }
    }
    
    // ==================== ABSTRACT METODLAR ====================
    // Alt sınıflar MUTLAKA implement etmeli
    
    /**
     * Maç işleme - Alt sınıflar kendi mantığını yazar
     * @param mac İşlenecek maç
     */
    protected abstract void maciIsle(Mac mac);
    
    /**
     * İşlem tipini döndür
     * @return İşlem tipi (örn: "OLUŞTURMA", "ONAYLAMA", "GÜNCELLEME")
     */
    protected abstract String islemTipi();
    
    // ==================== CONCRETE METODLAR ====================
    // Varsayılan implementasyon var, override edilebilir
    
    /**
     * Ön kontroller - Override edilebilir
     * @param mac Kontrol edilecek maç
     * @return Kontroller başarılı mı?
     */
    protected boolean onKontrollerYap(Mac mac) {
        logger.debug("🔍 Ön kontroller yapılıyor...");
        
        if (mac == null) {
            logger.error("Maç null olamaz!");
            return false;
        }
        
        if (mac.getTarih() == null) {
            logger.error("Maç tarihi zorunludur!");
            return false;
        }
        
        if (mac.getSaat() == null) {
            logger.error("Maç saati zorunludur!");
            return false;
        }
        
        logger.debug("✓ Ön kontroller başarılı");
        return true;
    }
    
    /**
     * Veri doğrulama - Override edilebilir
     * @param mac Doğrulanacak maç
     * @return Doğrulama başarılı mı?
     */
    protected boolean verileriDogrula(Mac mac) {
        logger.debug("✓ Veri doğrulama yapılıyor...");
        
        // Tarih geçmiş olmamalı (oluşturma için)
        if (mac.getId() == null && mac.getTarih().isBefore(java.time.LocalDate.now())) {
            logger.warn("⚠️ Maç tarihi geçmişte!");
        }
        
        logger.debug("✓ Veri doğrulama başarılı");
        return true;
    }
    
    /**
     * Kaydetme işlemi - Override edilebilir
     * @param mac Kaydedilecek maç
     */
    protected void kaydet(Mac mac) {
        logger.info("💾 Maç kaydediliyor: {}", islemTipi());
        // Varsayılan implementasyon - alt sınıflar override edebilir
    }
    
    /**
     * Hata yönetimi - Override edilebilir
     * @param mac Hatalı maç
     * @param e Oluşan hata
     */
    protected void hataYonet(Mac mac, Exception e) {
        logger.error("⚠️ Hata yönetimi: {} - {}", islemTipi(), e.getMessage());
        // Varsayılan hata yönetimi
    }
    
    // ==================== HOOK METODLAR ====================
    // Opsiyonel - Alt sınıflar isterse override eder
    
    /**
     * Bildirim gönderilsin mi? (Hook method)
     * @return true ise bildirim gönderilir
     */
    protected boolean bildirimGonder() {
        return false; // Varsayılan: bildirim gönderilmez
    }
    
    /**
     * Bildirim gönderme implementasyonu
     * @param mac İlgili maç
     */
    protected void bildirimGonderImpl(Mac mac) {
        logger.info("📧 Varsayılan bildirim gönderimi");
    }
    
    /**
     * Son işlemler (Hook method)
     * @param mac İşlenen maç
     */
    protected void sonIslemler(Mac mac) {
        logger.debug("🏁 İşlem tamamlandı: {}", islemTipi());
    }
    
    /**
     * Log mesajı oluştur
     * @return Detaylı log mesajı
     */
    protected String logMesaji() {
        return String.format("Maç İşleme: %s", islemTipi());
    }
}

