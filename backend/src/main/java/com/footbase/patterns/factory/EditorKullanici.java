package com.footbase.patterns.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Editör Kullanıcı (Concrete Product - Factory Pattern)
 * 
 * Editör kullanıcı tipini temsil eder.
 * Maç ekleme ve yönetme yetkilerine sahiptir.
 * 
 * YETKİLER:
 * - Maç ekleme
 * - Maç bilgilerini güncelleme
 * - Gol/kart girişi
 * - Skor güncelleme
 * - Kendi maçlarını görme
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public class EditorKullanici implements Kullanici {
    
    private static final Logger logger = LoggerFactory.getLogger(EditorKullanici.class);
    private String displayName;
    
    /**
     * Constructor
     */
    public EditorKullanici() {
        this.displayName = "Editor";
        logger.info("✍️ EditorKullanici oluşturuldu (Factory Pattern)");
    }
    
    /**
     * Constructor with display name
     * @param displayName Görünen ad
     */
    public EditorKullanici(String displayName) {
        this.displayName = displayName;
        logger.info("✍️ EditorKullanici oluşturuldu: {}", displayName);
    }
    
    @Override
    public void login() {
        logger.info("✍️ Editör giriş yaptı: {}", displayName);
        // Editör özel giriş işlemleri
        logger.debug("Editör paneli yükleniyor...");
        logger.debug("Bekleyen maçlar kontrol ediliyor...");
    }
    
    @Override
    public List<String> getPermissions() {
        // Editör SADECE maç yönetimi yetkileri
        return Arrays.asList(
            // Maç yetkileri
            "MATCH_CREATE",
            "MATCH_EDIT_OWN",      // Sadece kendi maçlarını
            "MATCH_VIEW_OWN",      // Sadece kendi maçlarını
            "MATCH_UPDATE_SCORE",  // Skor güncelleme
            "MATCH_ADD_EVENT",     // Gol/kart ekleme
            "MATCH_START",         // Maç başlatma
            "MATCH_FINISH",        // Maç bitirme
            
            // Sınırlı yetikler
            "COMMENT_ADD",         // Yorum ekleyebilir
            "VIEW_OWN_STATS"       // Kendi istatistiklerini görebilir
        );
    }
    
    @Override
    public String getRole() {
        return "EDITOR";
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
        return "EditorKullanici{" +
                "displayName='" + displayName + '\'' +
                ", role='" + getRole() + '\'' +
                ", permissions=" + getPermissions().size() +
                '}';
    }
}

