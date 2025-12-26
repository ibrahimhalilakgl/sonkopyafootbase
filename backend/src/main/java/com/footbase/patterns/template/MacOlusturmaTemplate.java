package com.footbase.patterns.template;

import com.footbase.entity.Mac;
import com.footbase.repository.MacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Maç Oluşturma Template (Concrete Template)
 * 
 * Yeni maç oluşturma işlemini gerçekleştirir.
 * Template Method Pattern'in concrete implementasyonu.
 * 
 * ÖZELLEŞTİRİLEN ADIMLAR:
 * - Maç işleme: Yeni maç kaydı
 * - Bildirim: Admin'e bildirim gönderilir
 * - Validasyon: Takım bilgileri kontrolü
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class MacOlusturmaTemplate extends MacIslemSablonu {
    
    @Autowired(required = false)
    private MacRepository macRepository;
    
    @Override
    protected void maciIsle(Mac mac) {
        logger.info("🆕 Yeni maç oluşturuluyor...");
        
        // Maç durumunu ayarla
        if (mac.getDurum() == null) {
            mac.setDurum("Planlandı");
        }
        
        // Onay durumunu ayarla
        if (mac.getOnayDurumu() == null) {
            mac.setOnayDurumu("ONAY_BEKLIYOR");
        }
        
        logger.info("✅ Maç oluşturuldu - Onay bekliyor");
    }
    
    @Override
    protected String islemTipi() {
        return "MAC_OLUSTURMA";
    }
    
    @Override
    protected boolean verileriDogrula(Mac mac) {
        // Önce parent'ın doğrulamasını yap
        if (!super.verileriDogrula(mac)) {
            return false;
        }
        
        logger.debug("🔍 Maç oluşturma için ek doğrulamalar...");
        
        // Maç ID'si olmamalı (yeni maç)
        if (mac.getId() != null) {
            logger.error("❌ Yeni maç için ID olmamalı!");
            return false;
        }
        
        // Takım kontrolü (macTakimlari üzerinden)
        if (mac.getMacTakimlari() == null || mac.getMacTakimlari().size() < 2) {
            logger.error("❌ Maç için en az 2 takım gerekli!");
            return false;
        }
        
        logger.debug("✓ Maç oluşturma doğrulaması başarılı");
        return true;
    }
    
    @Override
    protected void kaydet(Mac mac) {
        logger.info("💾 Yeni maç veritabanına kaydediliyor...");
        
        if (macRepository != null) {
            try {
                macRepository.save(mac);
                logger.info("✅ Maç başarıyla kaydedildi - ID: {}", mac.getId());
            } catch (Exception e) {
                logger.error("❌ Maç kaydetme hatası: {}", e.getMessage());
                throw new RuntimeException("Maç kaydedilemedi", e);
            }
        } else {
            logger.warn("⚠️ MacRepository bulunamadı - test modu");
        }
    }
    
    @Override
    protected boolean bildirimGonder() {
        return true; // Yeni maç oluşturulduğunda bildirim gönder
    }
    
    @Override
    protected void bildirimGonderImpl(Mac mac) {
        logger.info("📧 Admin'e yeni maç bildirimi gönderiliyor...");
        logger.info("📝 Konu: Yeni Maç Onay Bekliyor");
        logger.info("📅 Tarih: {} {}", mac.getTarih(), mac.getSaat());
        // Burada gerçek bildirim servisi çağrılabilir
    }
    
    @Override
    protected void sonIslemler(Mac mac) {
        logger.info("🏁 Maç oluşturma işlemi tamamlandı");
        logger.info("📊 Durum: {}, Onay: {}", mac.getDurum(), mac.getOnayDurumu());
    }
}

