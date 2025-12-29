package com.footbase.patterns.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Command Pattern - Command History
 * İşlem geçmişini tutar ve undo/redo özelliği sağlar
 */
@Component
public class CommandHistory {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandHistory.class);
    
    // Çalıştırılmış komutlar (undo için)
    private final Stack<Command> history = new Stack<>();
    
    // Geri alınmış komutlar (redo için)
    private final Stack<Command> redoStack = new Stack<>();
    
    // Maksimum geçmiş boyutu
    private static final int MAX_HISTORY_SIZE = 50;
    
    /**
     * Komutu geçmişe ekler
     */
    public void push(Command command) {
        if (history.size() >= MAX_HISTORY_SIZE) {
            // En eski komutu sil
            history.remove(0);
            logger.info("📚 Geçmiş dolu, en eski komut silindi");
        }
        history.push(command);
        // Yeni komut eklendiğinde redo stack'i temizle
        redoStack.clear();
        logger.info("📝 Komut geçmişe eklendi: {} (Toplam: {})", 
                command.getCommandType(), history.size());
    }
    
    /**
     * Son komutu geri alır (undo)
     */
    public boolean undo() {
        if (history.isEmpty()) {
            logger.warn("⚠️ Geri alınacak komut yok!");
            return false;
        }
        
        Command command = history.pop();
        boolean result = command.undo();
        
        if (result) {
            redoStack.push(command);
            logger.info("✅ Komut geri alındı: {}", command.getDescription());
        } else {
            // Başarısız olursa tekrar geçmişe ekle
            history.push(command);
            logger.error("❌ Komut geri alınamadı: {}", command.getDescription());
        }
        
        return result;
    }
    
    /**
     * Geri alınmış komutu tekrar yapar (redo)
     */
    public boolean redo() {
        if (redoStack.isEmpty()) {
            logger.warn("⚠️ Tekrar yapılacak komut yok!");
            return false;
        }
        
        Command command = redoStack.pop();
        boolean result = command.redo();
        
        if (result) {
            history.push(command);
            logger.info("✅ Komut tekrar yapıldı: {}", command.getDescription());
        } else {
            // Başarısız olursa redo stack'e geri koy
            redoStack.push(command);
            logger.error("❌ Komut tekrar yapılamadı: {}", command.getDescription());
        }
        
        return result;
    }
    
    /**
     * Belirli kullanıcıya ait son komutu geri alır
     */
    public boolean undoByKullaniciId(Long kullaniciId) {
        if (history.isEmpty()) {
            logger.warn("⚠️ Geri alınacak komut yok!");
            return false;
        }
        
        // Son komut bu kullanıcıya ait mi kontrol et
        Command lastCommand = history.peek();
        if (!lastCommand.getKullaniciId().equals(kullaniciId)) {
            logger.warn("⚠️ Son komut bu kullanıcıya ait değil! (Kullanıcı: {}, Komut sahibi: {})", 
                    kullaniciId, lastCommand.getKullaniciId());
            return false;
        }
        
        return undo();
    }
    
    /**
     * Tüm geçmişi temizler
     */
    public void clear() {
        int size = history.size() + redoStack.size();
        history.clear();
        redoStack.clear();
        logger.info("🧹 Komut geçmişi temizlendi ({} komut silindi)", size);
    }
    
    /**
     * Geçmişi listeler
     */
    public List<Command> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Redo stack'i listeler
     */
    public List<Command> getRedoStack() {
        return new ArrayList<>(redoStack);
    }
    
    /**
     * Geçmiş boyutu
     */
    public int size() {
        return history.size();
    }
    
    /**
     * Redo stack boyutu
     */
    public int redoSize() {
        return redoStack.size();
    }
    
    /**
     * Geçmiş boş mu?
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }
    
    /**
     * Redo stack boş mu?
     */
    public boolean isRedoEmpty() {
        return redoStack.isEmpty();
    }
    
    /**
     * Son komutu göster (undo yapmadan)
     */
    public Command peek() {
        if (history.isEmpty()) {
            return null;
        }
        return history.peek();
    }
    
    /**
     * Geçmişi özet olarak logla
     */
    public void printHistory() {
        logger.info("📚 ========== KOMUT GEÇMİŞİ ==========");
        logger.info("📝 Toplam komut: {}", history.size());
        logger.info("🔄 Redo stack: {}", redoStack.size());
        
        if (!history.isEmpty()) {
            logger.info("📋 Son komutlar:");
            int count = Math.min(5, history.size());
            for (int i = history.size() - 1; i >= history.size() - count; i--) {
                Command cmd = history.get(i);
                logger.info("  {}. {} - {}", 
                        (i + 1), 
                        cmd.getCommandType(), 
                        cmd.getDescription());
            }
        }
        
        logger.info("========================================");
    }
}


