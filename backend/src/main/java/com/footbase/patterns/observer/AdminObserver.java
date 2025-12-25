package com.footbase.patterns.observer;

import com.footbase.entity.Mac;
import com.footbase.entity.Kullanici;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Admin Observer (Observer Pattern)
 * Maç onay işlemlerinden haberdar olmak isteyen admin'leri temsil eder
 */
public class AdminObserver implements Observer {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminObserver.class);
    
    private final Long adminId;
    private final String adminEmail;
    
    /**
     * AdminObserver constructor
     * @param admin Admin kullanıcı
     */
    public AdminObserver(Kullanici admin) {
        this.adminId = admin.getId();
        this.adminEmail = admin.getEmail();
    }
    
    /**
     * AdminObserver constructor
     * @param adminId Admin ID'si
     * @param adminEmail Admin email'i
     */
    public AdminObserver(Long adminId, String adminEmail) {
        this.adminId = adminId;
        this.adminEmail = adminEmail;
    }
    
    /**
     * Subject'ten gelen bildirimi işler
     * @param eventType Olay tipi
     * @param data Olayla ilgili veri
     */
    @Override
    public void update(String eventType, Object data) {
        if (data instanceof Mac) {
            Mac mac = (Mac) data;
            handleMacEvent(eventType, mac);
        }
    }
    
    /**
     * Maç olaylarını işler
     * @param eventType Olay tipi
     * @param mac Maç
     */
    private void handleMacEvent(String eventType, Mac mac) {
        switch (eventType) {
            case "MAC_EKLENDI":
                logger.info("Admin {} ({}) için yeni maç bildirimi: Maç ID={}", 
                    adminId, adminEmail, mac.getId());
                // Burada gerçek bildirim sistemi entegre edilebilir (email, push notification vb.)
                break;
            case "MAC_ONAYLANDI":
                logger.info("Admin {} ({}) için maç onay bildirimi: Maç ID={}", 
                    adminId, adminEmail, mac.getId());
                break;
            case "MAC_REDDEDILDI":
                logger.info("Admin {} ({}) için maç red bildirimi: Maç ID={}", 
                    adminId, adminEmail, mac.getId());
                break;
            default:
                logger.warn("Bilinmeyen olay tipi: {}", eventType);
        }
    }
    
    /**
     * Admin ID'sini döndürür
     * @return Admin ID
     */
    public Long getAdminId() {
        return adminId;
    }
    
    /**
     * Admin email'ini döndürür
     * @return Admin email
     */
    public String getAdminEmail() {
        return adminEmail;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AdminObserver that = (AdminObserver) obj;
        return adminId != null && adminId.equals(that.adminId);
    }
    
    @Override
    public int hashCode() {
        return adminId != null ? adminId.hashCode() : 0;
    }
}

