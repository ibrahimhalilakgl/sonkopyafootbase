package com.footbase.patterns.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Admin Kullanıcı (Concrete Product - Factory Pattern)
 * 
 * Yönetici kullanıcı tipini temsil eder.
 * Tüm yetkilere sahiptir.
 * 
 * YETKİLER:
 * - Maç onaylama/reddetme
 * - Kullanıcı yönetimi
 * - Editör atama
 * - Sistem ayarları
 * - Tüm veri erişimi
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public class AdminKullanici implements Kullanici {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminKullanici.class);
    private String displayName;
    
    /**
     * Constructor
     */
    public AdminKullanici() {
        this.displayName = "Admin";
        logger.info("🔑 AdminKullanici oluşturuldu (Factory Pattern)");
    }
    
    /**
     * Constructor with display name
     * @param displayName Görünen ad
     */
    public AdminKullanici(String displayName) {
        this.displayName = displayName;
        logger.info("🔑 AdminKullanici oluşturuldu: {}", displayName);
    }
    
    @Override
    public void login() {
        logger.info("👨‍💼 Admin giriş yaptı: {}", displayName);
        // Admin özel giriş işlemleri
        logger.debug("Admin paneli yükleniyor...");
        logger.debug("Onay bekleyen maçlar kontrol ediliyor...");
    }
    
    @Override
    public List<String> getPermissions() {
        // Admin TÜM yetkilere sahip
        return Arrays.asList(
            // Maç yetkileri
            "MATCH_CREATE",
            "MATCH_EDIT",
            "MATCH_DELETE",
            "MATCH_APPROVE",
            "MATCH_REJECT",
            "MATCH_PUBLISH",
            
            // Kullanıcı yetkileri
            "USER_CREATE",
            "USER_EDIT",
            "USER_DELETE",
            "USER_ASSIGN_ROLE",
            
            // Editör yönetimi
            "EDITOR_ASSIGN",
            "EDITOR_MANAGE",
            
            // Takım ve oyuncu yetkileri
            "TEAM_MANAGE",
            "PLAYER_MANAGE",
            
            // Yorum yönetimi
            "COMMENT_MODERATE",
            "COMMENT_DELETE",
            
            // Sistem yetkileri
            "SYSTEM_SETTINGS",
            "VIEW_ANALYTICS",
            "VIEW_LOGS"
        );
    }
    
    @Override
    public String getRole() {
        return "ADMIN";
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Display name setter
     * @param displayName Yeni görünen ad
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return "AdminKullanici{" +
                "displayName='" + displayName + '\'' +
                ", role='" + getRole() + '\'' +
                ", permissions=" + getPermissions().size() +
                '}';
    }
}

