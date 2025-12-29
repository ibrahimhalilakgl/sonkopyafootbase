package com.footbase.patterns.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Command Pattern - Abstract Base Command
 * Tüm maç komutları için temel sınıf
 */
public abstract class MacCommand implements Command {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected Long kullaniciId;
    protected LocalDateTime executionTime;
    protected boolean executed = false;
    
    public MacCommand(Long kullaniciId) {
        this.kullaniciId = kullaniciId;
        this.executionTime = LocalDateTime.now();
    }
    
    @Override
    public boolean execute() {
        try {
            logger.info("🎯 {} çalıştırılıyor... (Kullanıcı: {})", getCommandType(), kullaniciId);
            boolean result = doExecute();
            if (result) {
                executed = true;
                logger.info("✅ {} başarılı!", getCommandType());
            } else {
                logger.warn("❌ {} başarısız!", getCommandType());
            }
            return result;
        } catch (Exception e) {
            logger.error("💥 {} hatası: {}", getCommandType(), e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean undo() {
        if (!executed) {
            logger.warn("⚠️ {} henüz çalıştırılmadı, geri alınamaz!", getCommandType());
            return false;
        }
        try {
            logger.info("🔄 {} geri alınıyor...", getCommandType());
            boolean result = doUndo();
            if (result) {
                executed = false;
                logger.info("✅ {} geri alındı!", getCommandType());
            } else {
                logger.warn("❌ {} geri alınamadı!", getCommandType());
            }
            return result;
        } catch (Exception e) {
            logger.error("💥 {} geri alma hatası: {}", getCommandType(), e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean redo() {
        if (executed) {
            logger.warn("⚠️ {} zaten çalıştırılmış, tekrar yapılamaz!", getCommandType());
            return false;
        }
        logger.info("🔁 {} tekrar yapılıyor...", getCommandType());
        return execute();
    }
    
    /**
     * Alt sınıflar bu metodu implement eder (execute mantığı)
     */
    protected abstract boolean doExecute();
    
    /**
     * Alt sınıflar bu metodu implement eder (undo mantığı)
     */
    protected abstract boolean doUndo();
    
    @Override
    public Long getKullaniciId() {
        return kullaniciId;
    }
    
    @Override
    public LocalDateTime getExecutionTime() {
        return executionTime;
    }
    
    public boolean isExecuted() {
        return executed;
    }
}


