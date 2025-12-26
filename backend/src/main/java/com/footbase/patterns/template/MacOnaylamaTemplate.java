package com.footbase.patterns.template;

import com.footbase.entity.Mac;
import com.footbase.repository.MacDurumGecmisiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Maç Onaylama Template (Concrete Template)
 * 
 * Maç onaylama işlemini gerçekleştirir.
 * Admin tarafından maçın yayına alınması.
 * 
 * ÖZELLEŞTİRİLEN ADIMLAR:
 * - Maç işleme: Onay durumu güncelleme
 * - Bildirim: Editöre bildirim gönderilir
 * - Geçmiş: Durum geçmişine kayıt
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@Component
public class MacOnaylamaTemplate extends MacIslemSablonu {
    
    @Autowired(required = false)
    private MacDurumGecmisiRepository durumGecmisiRepository;
    
    private boolean onaylandiMi = true; // true: onayla, false: reddet
    
    @Override
    protected void maciIsle(Mac mac) {
        logger.info("✅ Maç onaylanıyor...");
        
        if (onaylandiMi) {
            mac.setOnayDurumu("YAYINDA");
            logger.info("✅ Maç YAYINDA durumuna getirildi");
        } else {
            mac.setOnayDurumu("REDDEDILDI");
            logger.warn("❌ Maç REDDEDİLDİ");
        }
    }
    
    @Override
    protected String islemTipi() {
        return onaylandiMi ? "MAC_ONAYLAMA" : "MAC_REDDETME";
    }
    
    @Override
    protected boolean onKontrollerYap(Mac mac) {
        if (!super.onKontrollerYap(mac)) {
            return false;
        }
        
        logger.debug("🔍 Onaylama için ek kontroller...");
        
        // Maç ID'si olmalı (mevcut maç)
        if (mac.getId() == null) {
            logger.error("❌ Onaylanacak maçın ID'si olmalı!");
            return false;
        }
        
        // Onay durumu kontrolü
        if (!"ONAY_BEKLIYOR".equals(mac.getOnayDurumu())) {
            logger.error("❌ Sadece ONAY_BEKLIYOR durumundaki maçlar onaylanabilir!");
            return false;
        }
        
        logger.debug("✓ Onaylama kontrolleri başarılı");
        return true;
    }
    
    @Override
    protected void kaydet(Mac mac) {
        logger.info("💾 Onay durumu kaydediliyor...");
        // Burada MacService üzerinden kayıt yapılabilir
    }
    
    @Override
    protected boolean bildirimGonder() {
        return true; // Onaylama/Red durumunda bildirim gönder
    }
    
    @Override
    protected void bildirimGonderImpl(Mac mac) {
        if (onaylandiMi) {
            logger.info("📧 Editöre maç onaylandı bildirimi gönderiliyor...");
            logger.info("✅ Maç yayına alındı - ID: {}", mac.getId());
        } else {
            logger.info("📧 Editöre maç reddedildi bildirimi gönderiliyor...");
            logger.warn("❌ Maç reddedildi - ID: {}", mac.getId());
        }
    }
    
    @Override
    protected void sonIslemler(Mac mac) {
        logger.info("📝 Durum geçmişine kaydediliyor...");
        // MacDurumGecmisi kaydı yapılabilir
        
        if (onaylandiMi) {
            logger.info("🎉 Maç başarıyla yayına alındı!");
        } else {
            logger.info("🚫 Maç reddedildi");
        }
    }
    
    /**
     * Onaylama/Reddetme ayarı
     * @param onayla true: onayla, false: reddet
     */
    public void setOnayla(boolean onayla) {
        this.onaylandiMi = onayla;
    }
}

