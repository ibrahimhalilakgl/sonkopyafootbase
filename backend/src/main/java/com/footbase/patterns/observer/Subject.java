package com.footbase.patterns.observer;

/**
 * Subject interface (Observer Pattern)
 * Observer'ları yöneten ve durum değişikliklerini bildiren sınıflar için arayüz
 */
public interface Subject {
    
    /**
     * Observer'ı listeye ekler
     * @param observer Eklenecek observer
     */
    void attach(Observer observer);
    
    /**
     * Observer'ı listeden çıkarır
     * @param observer Çıkarılacak observer
     */
    void detach(Observer observer);
    
    /**
     * Tüm observer'lara bildirim gönderir
     */
    void notifyObservers();
}

