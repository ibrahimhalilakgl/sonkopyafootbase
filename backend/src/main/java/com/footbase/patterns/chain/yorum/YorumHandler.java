package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.Handler;

public abstract class YorumHandler extends Handler<Yorum> {
    
    protected void logYorumAction(Yorum yorum, String action) {
        logger.info("💬 [{}] Yorum ID: {}, İşlem: {}", 
                   getHandlerName(), 
                   yorum.getId() != null ? yorum.getId() : "YENİ", 
                   action);
    }
}
