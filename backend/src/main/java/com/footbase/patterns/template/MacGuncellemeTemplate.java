package com.footbase.patterns.template;

import com.footbase.entity.Mac;
import com.footbase.repository.MacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Maç Güncelleme Template (Concrete Template)
 * 
 * Mevcut maç güncelleme işlemini gerçekleştirir.
 * Editör tarafından maç bilgilerinin değiştirilmesi.
 * 
 * ÖZELLEŞTİRİLEN ADIMLAR:
 * - Maç işleme: Mevcut maç güncelleme
 * - Bildirim: Gerekirse admin'e bildirim
 * - Validasyon: Değişikliklerin kontrolü
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class MacGuncellemeTemplate extends MacIslemSablonu {
    
    @Autowired(required = false)
    private MacRepository macRepository;
    
    private Mac eskiMac; // Değişiklik karşılaştırması için
    
    @Override
    protected void maciIsle(Mac mac) {
        logger.info("✏️ Maç güncelleniyor...");
        
        // Değişiklikleri logla
        if (eskiMac != null) {
            if (!eskiMac.getTarih().equals(mac.getTarih())) {
                logger.info("📅 Tarih değişti: {} → {}", eskiMac.getTarih(), mac.getTarih());
            }
            if (!eskiMac.getSaat().equals(mac.getSaat())) {
                logger.info("🕐 Saat değişti: {} → {}", eskiMac.getSaat(), mac.getSaat());
            }
        }
        
        logger.info("✅ Maç bilgileri güncellendi");
    }
    
    @Override
    protected String islemTipi() {
        return "MAC_GUNCELLEME";
    }
    
    @Override
    protected boolean onKontrollerYap(Mac mac) {
        if (!super.onKontrollerYap(mac)) {
            return false;
        }
        
        logger.debug("🔍 Güncelleme için ek kontroller...");
        
        // Maç ID'si olmalı (mevcut maç)
        if (mac.getId() == null) {
            logger.error("❌ Güncellenecek maçın ID'si olmalı!");
            return false;
        }
        
        // Eski maçı al (karşılaştırma için)
        if (macRepository != null) {
            try {
                eskiMac = macRepository.findById(mac.getId()).orElse(null);
                if (eskiMac == null) {
                    logger.error("❌ Maç bulunamadı!");
                    return false;
                }
            } catch (Exception e) {
                logger.warn("⚠️ Eski maç bilgisi alınamadı: {}", e.getMessage());
            }
        }
        
        logger.debug("✓ Güncelleme kontrolleri başarılı");
        return true;
    }
    
    @Override
    protected boolean verileriDogrula(Mac mac) {
        if (!super.verileriDogrula(mac)) {
            return false;
        }
        
        logger.debug("🔍 Güncelleme için veri doğrulama...");
        
        // Eğer maç yayında ise bazı değişiklikler yapılamaz
        if ("YAYINDA".equals(mac.getOnayDurumu())) {
            logger.info("⚠️ Maç YAYINDA - bazı kısıtlamalar var");
            // Örnek: Tarih değişikliği yapılamaz
            if (eskiMac != null && !eskiMac.getTarih().equals(mac.getTarih())) {
                logger.warn("⚠️ Yayındaki maçın tarihi değiştirilemez!");
                mac.setTarih(eskiMac.getTarih()); // Eski tarihe geri al
            }
        }
        
        logger.debug("✓ Güncelleme doğrulaması başarılı");
        return true;
    }
    
    @Override
    protected void kaydet(Mac mac) {
        logger.info("💾 Maç güncellemeleri kaydediliyor...");
        
        if (macRepository != null) {
            try {
                macRepository.save(mac);
                logger.info("✅ Maç başarıyla güncellendi - ID: {}", mac.getId());
            } catch (Exception e) {
                logger.error("❌ Maç güncelleme hatası: {}", e.getMessage());
                throw new RuntimeException("Maç güncellenemedi", e);
            }
        } else {
            logger.warn("⚠️ MacRepository bulunamadı - test modu");
        }
    }
    
    @Override
    protected boolean bildirimGonder() {
        // Sadece önemli değişikliklerde bildirim gönder
        if (eskiMac != null) {
            boolean onemliDegisiklik = 
                !eskiMac.getTarih().equals(eskiMac.getTarih()) ||
                !eskiMac.getSaat().equals(eskiMac.getSaat());
            return onemliDegisiklik;
        }
        return false;
    }
    
    @Override
    protected void bildirimGonderImpl(Mac mac) {
        logger.info("📧 Maç güncelleme bildirimi gönderiliyor...");
        logger.info("✏️ Maç bilgileri değiştirildi - ID: {}", mac.getId());
    }
    
    @Override
    protected void sonIslemler(Mac mac) {
        logger.info("🏁 Maç güncelleme işlemi tamamlandı");
        logger.info("📊 Güncel Durum: {}", mac.getDurum());
    }
    
    /**
     * Eski maç bilgisini ayarla (karşılaştırma için)
     * @param eskiMac Eski maç
     */
    public void setEskiMac(Mac eskiMac) {
        this.eskiMac = eskiMac;
    }
}

