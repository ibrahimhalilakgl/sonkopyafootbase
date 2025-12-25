package com.footbase.patterns.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Normal Kullanıcı (Concrete Product - Factory Pattern)
 * 
 * Standart kullanıcı tipini temsil eder.
 * Sadece görüntüleme ve yorum yapma yetkilerine sahiptir.
 * 
 * YETKİLER:
 * - Maç görüntüleme
 * - Oyuncu/takım görüntüleme
 * - Yorum yapma
 * - Oyuncu puanlama
 * - Profil yönetimi
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public class NormalKullanici implements Kullanici {
    
    private static final Logger logger = LoggerFactory.getLogger(NormalKullanici.class);
    private String displayName;
    
    /**
     * Constructor
     */
    public NormalKullanici() {
        this.displayName = "User";
        logger.info("👤 NormalKullanici oluşturuldu (Factory Pattern)");
    }
    
    /**
     * Constructor with display name
     * @param displayName Görünen ad
     */
    public NormalKullanici(String displayName) {
        this.displayName = displayName;
        logger.info("👤 NormalKullanici oluşturuldu: {}", displayName);
    }
    
    @Override
    public void login() {
        logger.info("👤 Kullanıcı giriş yaptı: {}", displayName);
        // Normal kullanıcı giriş işlemleri
        logger.debug("Ana sayfa yükleniyor...");
        logger.debug("Son maçlar getiriliyor...");
    }
    
    @Override
    public List<String> getPermissions() {
        // Normal kullanıcı SADECE görüntüleme ve etkileşim yetkileri
        return Arrays.asList(
            // Görüntüleme yetkileri
            "MATCH_VIEW",
            "PLAYER_VIEW",
            "TEAM_VIEW",
            "STATS_VIEW",
            
            // Etkileşim yetkileri
            "COMMENT_ADD",
            "COMMENT_EDIT_OWN",    // Sadece kendi yorumlarını
            "COMMENT_DELETE_OWN",  // Sadece kendi yorumlarını
            "PLAYER_RATE",         // Oyuncu puanlama
            "MATCH_PREDICT",       // Maç tahmini
            
            // Profil yetkileri
            "PROFILE_EDIT_OWN",
            "VIEW_OWN_ACTIVITY"
        );
    }
    
    @Override
    public String getRole() {
        return "USER";
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
        return "NormalKullanici{" +
                "displayName='" + displayName + '\'' +
                ", role='" + getRole() + '\'' +
                ", permissions=" + getPermissions().size() +
                '}';
    }
}

