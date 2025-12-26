package com.footbase.patterns.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Handler<T> {
    
    protected static final Logger logger = LoggerFactory.getLogger(Handler.class);
    protected Handler<T> next;
    protected int priority = 0;
    
    public Handler<T> setNext(Handler<T> next) {
        this.next = next;
        return next;
    }
    
    public final HandlerResult handle(T request) {
        logger.debug("🔗 [{}] işleniyor...", this.getClass().getSimpleName());
        
        HandlerResult result = doHandle(request);
        
        if (!result.isSuccess()) {
            logger.warn("❌ [{}] başarısız: {}", this.getClass().getSimpleName(), result.getMessage());
            return result;
        }
        
        logger.debug("✅ [{}] başarılı", this.getClass().getSimpleName());
        
        if (next != null) {
            return next.handle(request);
        }
        
        return HandlerResult.success("Tüm kontroller başarılı");
    }
    
    protected abstract HandlerResult doHandle(T request);
    
    public String getHandlerName() {
        return this.getClass().getSimpleName();
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public String visualizeChain() {
        StringBuilder sb = new StringBuilder();
        Handler<T> current = this;
        int index = 1;
        
        while (current != null) {
            sb.append(String.format("%d. %s", index++, current.getHandlerName()));
            if (current.next != null) {
                sb.append(" → ");
            }
            current = current.next;
        }
        
        return sb.toString();
    }
}

