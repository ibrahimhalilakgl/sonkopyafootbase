package com.footbase.patterns.observer;

import com.footbase.entity.Mac;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Mac Approval Subject (Observer Pattern)
 * Maç onay işlemlerini yöneten ve admin'lere bildirim gönderen sınıf
 * Spring Component olarak yönetilir
 */
@Component
public class MacApprovalSubject implements Subject {
    
    private final List<Observer> observers = new ArrayList<>();
    private Mac mac;
    private String eventType;
    
    /**
     * Observer'ı listeye ekler
     * @param observer Eklenecek observer
     */
    @Override
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Observer'ı listeden çıkarır
     * @param observer Çıkarılacak observer
     */
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    /**
     * Tüm observer'lara bildirim gönderir
     */
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(eventType, mac);
        }
    }
    
    /**
     * Yeni maç eklendiğinde observer'ları bilgilendirir
     * @param mac Eklenen maç
     */
    public void macEklendi(Mac mac) {
        this.mac = mac;
        this.eventType = "MAC_EKLENDI";
        notifyObservers();
    }
    
    /**
     * Maç onaylandığında observer'ları bilgilendirir
     * @param mac Onaylanan maç
     */
    public void macOnaylandi(Mac mac) {
        this.mac = mac;
        this.eventType = "MAC_ONAYLANDI";
        notifyObservers();
    }
    
    /**
     * Maç reddedildiğinde observer'ları bilgilendirir
     * @param mac Reddedilen maç
     */
    public void macReddedildi(Mac mac) {
        this.mac = mac;
        this.eventType = "MAC_REDDEDILDI";
        notifyObservers();
    }
    
    /**
     * Mevcut observer sayısını döndürür
     * @return Observer sayısı
     */
    public int getObserverCount() {
        return observers.size();
    }
}

