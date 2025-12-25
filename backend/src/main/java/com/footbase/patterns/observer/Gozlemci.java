package com.footbase.patterns.observer;

/**
 * Gözlemci Arayüzü (Observer Pattern)
 * 
 * Bu arayüz, Observer Design Pattern'in temel yapı taşıdır.
 * Bir konuyu (Subject) takip eden ve durumundaki değişikliklerden
 * haberdar olmak isteyen tüm gözlemciler bu arayüzü implement eder.
 * 
 * OBSERVER PATTERN NEDİR?
 * Observer Pattern, nesneler arasında bire-çok ilişkisi tanımlar.
 * Bir nesne (Subject) durumu değiştiğinde, ona bağımlı tüm nesneler
 * (Observers) otomatik olarak bilgilendirilir ve güncellenir.
 * 
 * KULLANIM ÖRNEKLERİ:
 * 1. Bildirim Sistemleri: Bir olay olduğunda birden fazla kullanıcı bilgilendirilir
 * 2. MVC Pattern: Model değiştiğinde View otomatik güncellenir
 * 3. Event Handling: Button'a tıklandığında listener'lar tetiklenir
 * 4. Pub-Sub Sistemleri: Mesaj yayınlandığında abone olanlar haberdar olur
 * 
 * FOOTBASE'DE KULLANIMI:
 * - Editör maç eklediğinde → Admin bilgilendirilir
 * - Admin maçı onayladığında → Editör bilgilendirilir
 * - Maç başladığında → Takipçiler bilgilendirilir
 * - Gol atıldığında → Maç takipçileri bilgilendirilir
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
public interface Gozlemci {
    
    /**
     * Konudan (Subject) gelen güncelleme bildirimini işler
     * 
     * Bu metod, takip edilen konuda bir değişiklik olduğunda
     * otomatik olarak çağrılır. Gözlemci, gelen bilgilere göre
     * kendi iç durumunu güncelleyebilir veya başka işlemler yapabilir.
     * 
     * @param olayTipi Gerçekleşen olayın tipi
     *                 Örnek: "MAC_EKLENDI", "MAC_ONAYLANDI", "GOL_ATILDI"
     * 
     * @param veri Olayla ilgili veri nesnesi
     *             Tip güvenliği için instanceof kontrolü yapılmalıdır
     *             Örnek: Mac, Oyuncu, Yorum nesnesi olabilir
     */
    void guncelle(String olayTipi, Object veri);
}

