package com.footbase.patterns.observer;

/**
 * Konu Arayüzü (Subject - Observer Pattern)
 * 
 * Bu arayüz, Observer Design Pattern'deki Subject (Konu) rolünü üstlenir.
 * Gözlemcileri (Observer) yöneten ve durum değişikliklerini onlara bildiren
 * sınıflar bu arayüzü implement eder.
 * 
 * SUBJECT'İN SORUMLULUKLARI:
 * 1. Gözlemcileri kaydetme (attach/ekle)
 * 2. Gözlemcileri çıkarma (detach/cikar)
 * 3. Durum değiştiğinde tüm gözlemcilere bildirim gönderme (notify/bilgilendir)
 * 
 * ÇALIŞMA PRENSİBİ:
 * 1. Gözlemciler kendilerini konuya kaydeder (ekle metoduyla)
 * 2. Konuda önemli bir olay gerçekleşir
 * 3. Konu, tüm kayıtlı gözlemcilere bildirim gönderir (bilgilendir metoduyla)
 * 4. Her gözlemci bildirimi alır ve kendi işlemini yapar
 * 
 * FOOTBASE'DE KULLANIMI:
 * - MacOnayKonusu: Maç onay süreçlerini yönetir
 * - YorumKonusu: Yeni yorumları duyurur
 * - MacOlayKonusu: Maç içi olayları (gol, kart) duyurur
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public interface Konu {
    
    /**
     * Bir gözlemciyi kayıt listesine ekler
     * 
     * Eklenen gözlemci, artık bu konudaki değişikliklerden
     * haberdar olacaktır. Aynı gözlemci birden fazla kez
     * eklenmemelidir (implementasyonda kontrol edilmelidir).
     * 
     * @param gozlemci Eklenecek gözlemci
     */
    void ekle(Gozlemci gozlemci);
    
    /**
     * Bir gözlemciyi kayıt listesinden çıkarır
     * 
     * Çıkarılan gözlemci, artık bu konudaki değişikliklerden
     * haberdar olmayacaktır. Bu metod, performans optimizasyonu
     * ve bellek yönetimi için önemlidir.
     * 
     * @param gozlemci Çıkarılacak gözlemci
     */
    void cikar(Gozlemci gozlemci);
    
    /**
     * Kayıtlı tüm gözlemcilere bildirim gönderir
     * 
     * Bu metod, konuda önemli bir değişiklik olduğunda çağrılır.
     * Kayıtlı gözlemcilerin guncelle() metodu sırayla çağrılır
     * ve her gözlemci olay hakkında bilgilendirilir.
     * 
     * NOT: Bu işlem senkron çalışır. Eğer bir gözlemcinin
     * guncelle() metodu uzun sürerse, diğer gözlemciler bekler.
     * Asenkron bildirim için farklı implementasyon gerekir.
     */
    void gozlemcileriBilgilendir();
}

