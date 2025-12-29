package com.footbase.patterns.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Command Pattern - Command Invoker
 * Komutları çalıştırır ve geçmişi yönetir
 * 
 * Bu sınıf komutları çalıştırır ve geçmişe kaydeder.
 * Böylece undo/redo işlemleri yapılabilir.
 */
@Component
public class CommandInvoker {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandInvoker.class);
    
    @Autowired
    private CommandHistory commandHistory;
    
    /**
     * Komutu çalıştırır ve başarılıysa geçmişe ekler
     * 
     * @param command Çalıştırılacak komut
     * @return İşlem başarılıysa true
     */
    public boolean executeCommand(Command command) {
        logger.info("🎯 Komut çalıştırılıyor: {}", command.getDescription());
        
        boolean result = command.execute();
        
        if (result) {
            commandHistory.push(command);
            logger.info("✅ Komut başarıyla çalıştırıldı ve geçmişe eklendi");
        } else {
            logger.error("❌ Komut çalıştırılamadı: {}", command.getDescription());
        }
        
        return result;
    }
    
    /**
     * Son komutu geri alır
     * 
     * @return Geri alma başarılıysa true
     */
    public boolean undo() {
        logger.info("🔄 Son komut geri alınıyor...");
        return commandHistory.undo();
    }
    
    /**
     * Belirli kullanıcıya ait son komutu geri alır
     * 
     * @param kullaniciId Kullanıcı ID
     * @return Geri alma başarılıysa true
     */
    public boolean undoByKullaniciId(Long kullaniciId) {
        logger.info("🔄 Kullanıcı #{} için son komut geri alınıyor...", kullaniciId);
        return commandHistory.undoByKullaniciId(kullaniciId);
    }
    
    /**
     * Geri alınan komutu tekrar yapar
     * 
     * @return Tekrar başarılıysa true
     */
    public boolean redo() {
        logger.info("🔁 Komut tekrar yapılıyor...");
        return commandHistory.redo();
    }
    
    /**
     * Geçmişi temizler
     */
    public void clearHistory() {
        logger.info("🧹 Komut geçmişi temizleniyor...");
        commandHistory.clear();
    }
    
    /**
     * Geçmiş boyutu
     */
    public int getHistorySize() {
        return commandHistory.size();
    }
    
    /**
     * Redo stack boyutu
     */
    public int getRedoSize() {
        return commandHistory.redoSize();
    }
    
    /**
     * Geçmişi yazdır
     */
    public void printHistory() {
        commandHistory.printHistory();
    }
    
    /**
     * Geçmişten son komutu göster
     */
    public Command getLastCommand() {
        return commandHistory.peek();
    }
    
    /**
     * Command history'yi döndür (dış kullanım için)
     */
    public CommandHistory getCommandHistory() {
        return commandHistory;
    }
}


