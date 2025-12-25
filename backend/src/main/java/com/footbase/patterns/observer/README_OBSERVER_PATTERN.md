# OBSERVER PATTERN - BİLDİRİM SİSTEMİ

## 📋 İÇİNDEKİLER
1. [Observer Pattern Nedir?](#observer-pattern-nedir)
2. [Proje Yapısı](#proje-yapısı)
3. [Sınıflar ve Görevleri](#sınıflar-ve-görevleri)
4. [Kullanım Örnekleri](#kullanım-örnekleri)
5. [Veritabanı Tablosu](#veritabanı-tablosu)
6. [API Endpoints](#api-endpoints)
7. [Frontend Entegrasyonu](#frontend-entegrasyonu)

---

## 🎯 OBSERVER PATTERN NEDİR?

**Observer Pattern** (Gözlemci Deseni), nesneler arasında **bire-çok ilişkisi** tanımlayan davranışsal (behavioral) bir tasarım kalıbıdır.

### Temel Kavramlar:
- **Subject (Konu)**: Durum değişikliklerini duyuran nesne
- **Observer (Gözlemci)**: Durumu takip eden ve haberdar olan nesne

### Ne Zaman Kullanılır?
✅ Bir nesnedeki değişiklik başka nesneleri etkiliyorsa  
✅ Kaç nesnenin etkileneceği önceden bilinmiyorsa  
✅ Loose coupling (gevşek bağlılık) istiyorsanız  
✅ Bildirim sistemleri, event handling, pub-sub yapıları  

---

## 📁 PROJE YAPISI

```
backend/src/main/java/com/footbase/
├── patterns/observer/
│   ├── Gozlemci.java                    # Observer Interface
│   ├── Konu.java                        # Subject Interface
│   ├── YoneticiGozlemci.java            # Admin Observer (Concrete)
│   ├── EditorGozlemci.java              # Editor Observer (Concrete)
│   └── MacOnayKonusu.java               # Match Approval Subject (Concrete)
│
├── entity/
│   └── Bildirim.java                    # Notification Entity
│
├── repository/
│   └── BildirimRepository.java          # Notification Repository
│
├── service/
│   └── BildirimServisi.java             # Notification Service
│
└── controller/
    └── BildirimController.java          # Notification Controller
```

---

## 🏗️ SINIFLAR VE GÖREVLERİ

### 1. **Gozlemci** (Observer Interface)
```java
public interface Gozlemci {
    void guncelle(String olayTipi, Object veri);
}
```
**Görev**: Tüm gözlemcilerin implement etmesi gereken arayüz

### 2. **Konu** (Subject Interface)
```java
public interface Konu {
    void ekle(Gozlemci gozlemci);
    void cikar(Gozlemci gozlemci);
    void gozlemcileriBilgilendir();
}
```
**Görev**: Gözlemcileri yöneten ve bilgilendiren yapı

### 3. **YoneticiGozlemci** (Concrete Observer)
- **Rol**: Admin kullanıcılarını temsil eder
- **Bilgilendirildiği Durumlar**:
  - Editör maç eklediğinde
  - Maç onaylandığında
  - Maç reddedildiğinde
- **Yaptığı İşlemler**:
  - Bildirimi veritabanına kaydet
  - Loglara yaz
  - (Gelecekte: Email gönder)

### 4. **EditorGozlemci** (Concrete Observer)
- **Rol**: Editör kullanıcılarını temsil eder
- **Bilgilendirildiği Durumlar**:
  - Kendi maçı onaylandığında
  - Kendi maçı reddedildiğinde
- **Yaptığı İşlemler**:
  - Bildirimi veritabanına kaydet
  - Loglara yaz

### 5. **MacOnayKonusu** (Concrete Subject)
- **Rol**: Maç onay süreçlerini yönetir
- **Gözlemci Yönetimi**:
  - Gözlemcileri ekle/çıkar
  - Bildirim gönder
- **Olaylar**:
  - `macEklendi(Mac mac)`
  - `macOnaylandi(Mac mac)`
  - `macReddedildi(Mac mac)`
  - `macBasladi(Mac mac)`
  - `macBitti(Mac mac)`

### 6. **Bildirim** (Entity)
- **Görev**: Bildirimleri veritabanında saklar
- **Alanlar**:
  - Alıcı kullanıcı
  - Gönderici kullanıcı (opsiyonel)
  - Bildirim tipi
  - Başlık ve içerik
  - İlgili maç/oyuncu
  - Okundu durumu
  - Zaman bilgileri
  - Hedef URL

---

## 💡 KULLANIM ÖRNEKLERİ

### Örnek 1: Editör Maç Eklediğinde
```java
// MacService.java içinde

@Autowired
private MacOnayKonusu macOnayKonusu;

public Mac editorMacOlustur(Mac mac, Long editorId) {
    // ... maç kaydetme işlemleri ...
    
    // Admin'i gözlemci olarak ekle
    EditorYoneticileri ilişki = editorYoneticileriRepository.findByEditorId(editorId);
    YoneticiGozlemci adminGozlemci = new YoneticiGozlemci(ilişki.getAdmin());
    macOnayKonusu.ekle(adminGozlemci);
    
    // Maç eklendi olayını tetikle
    macOnayKonusu.macEklendi(kaydedilenMac);
    
    return kaydedilenMac;
}
```

### Örnek 2: Admin Maçı Onayladığında
```java
public void macOnayla(Long macId, Long adminId) {
    Mac mac = macRepository.findById(macId).orElseThrow();
    
    // Editörü gözlemci olarak ekle
    Long editorId = mac.getEditorId();
    Kullanici editor = kullaniciRepository.findById(editorId).orElseThrow();
    EditorGozlemci editorGozlemci = new EditorGozlemci(editor);
    macOnayKonusu.ekle(editorGozlemci);
    
    // Maç onaylandı olayını tetikle
    macOnayKonusu.macOnaylandi(mac);
    
    // Durum güncelle
    macDurumGecmisiRepository.saveMacDurumGecmisiNative(...);
}
```

---

## 🗄️ VERİTABANI TABLOSU

```sql
CREATE TABLE bildirimler (
    id BIGSERIAL PRIMARY KEY,
    alici_kullanici_id BIGINT NOT NULL,
    gonderici_kullanici_id BIGINT,
    bildirim_tipi VARCHAR(50) NOT NULL,
    baslik VARCHAR(255) NOT NULL,
    icerik TEXT NOT NULL,
    mac_id BIGINT,
    oyuncu_id BIGINT,
    okundu BOOLEAN NOT NULL DEFAULT FALSE,
    olusturma_zamani TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    okunma_zamani TIMESTAMP,
    hedef_url VARCHAR(500)
);
```

### Bildirim Tipleri:
- `MAC_EKLENDI`: Yeni maç onay bekliyor
- `MAC_ONAYLANDI`: Maç onaylandı
- `MAC_REDDEDILDI`: Maç reddedildi
- `MAC_BASLADI`: Maç başladı
- `MAC_BITTI`: Maç bitti
- `YENI_YORUM`: Yeni yorum eklendi
- `GOL_ATILDI`: Gol atıldı

---

## 🌐 API ENDPOINTS

### 1. Tüm Bildirimleri Getir
```
GET /api/notifications
Authorization: Bearer {token}
Response: Bildirim[]
```

### 2. Okunmamış Bildirimleri Getir
```
GET /api/notifications/unread
Authorization: Bearer {token}
Response: Bildirim[]
```

### 3. Okunmamış Sayı
```
GET /api/notifications/unread/count
Authorization: Bearer {token}
Response: { "count": 5 }
```

### 4. Son N Bildirimi Getir
```
GET /api/notifications/recent?limit=10
Authorization: Bearer {token}
Response: Bildirim[]
```

### 5. Bildirimi Okundu İşaretle
```
PUT /api/notifications/{id}/read
Authorization: Bearer {token}
Response: { "mesaj": "Bildirim okundu olarak işaretlendi" }
```

### 6. Tümünü Okundu İşaretle
```
PUT /api/notifications/read-all
Authorization: Bearer {token}
Response: { "mesaj": "5 bildirim okundu olarak işaretlendi" }
```

### 7. Bildirimi Sil
```
DELETE /api/notifications/{id}
Authorization: Bearer {token}
Response: { "mesaj": "Bildirim silindi" }
```

---

## 🎨 FRONTEND ENTEGRASYONU

### BildirimKutusu Komponenti
```jsx
import BildirimKutusu from 'components/Notification/BildirimKutusu';

// Header'da kullanım
<Header>
  <BildirimKutusu />
</Header>
```

### Özellikler:
✅ Okunmamış bildirim badge'i  
✅ Dropdown bildirim listesi  
✅ Tek tıkla okundu işaretle  
✅ Hedef sayfaya yönlendirme  
✅ 30 saniyede otomatik yenileme  
✅ Bildirim tiplerine göre renkli gösterim  

---

## 📊 OBSERVER PATTERN AKIŞ DİYAGRAMI

```
┌─────────────┐         ┌─────────────────┐        ┌──────────────────┐
│   Editör    │  Maç    │   MacOnayKonusu │ Notify │ YoneticiGozlemci │
│             │ Ekler   │    (Subject)    │───────>│    (Observer)    │
└─────────────┘         └─────────────────┘        └──────────────────┘
       │                         │                           │
       │ macEklendi()            │                           │
       │───────────────────────> │                           │
       │                         │                           │
       │                         │  guncelle()               │
       │                         │──────────────────────────>│
       │                         │                           │
       │                         │                           │ bildirimOlustur()
       │                         │                           │──────────────────>
       │                         │                           │   (Veritabanı)
```

---

## 🎓 ÖĞRENME NOKTALARI

### 1. **Loose Coupling**
Observer pattern sayesinde MacService, bildirim detaylarını bilmez.  
Sadece "mac eklendi" der, geri kalanını gözlemciler halleder.

### 2. **Open/Closed Principle**
Yeni bildirim tipi eklemek için mevcut kodu değiştirmemize gerek yok.  
Yeni gözlemci sınıfı oluştururuz.

### 3. **Single Responsibility**
- `MacOnayKonusu`: Sadece gözlemci yönetimi yapar
- `YoneticiGozlemci`: Sadece admin bildirimlerini işler
- `EditorGozlemci`: Sadece editör bildirimlerini işler

### 4. **Dependency Inversion**
Konkret sınıflara değil, arayüzlere bağımlıyız (Gozlemci, Konu).

---

## 🚀 GELECEK İYİLEŞTİRMELER

1. **Asenkron Bildirim**: CompletableFuture ile paralel gönderim
2. **Email Entegrasyonu**: JavaMail ile email gönderimi
3. **Push Notification**: Firebase ile mobil bildirim
4. **WebSocket**: Gerçek zamanlı bildirim
5. **Bildirim Önceliklendirme**: Önemli/Normal/Düşük
6. **Bildirim Gruplandırma**: "5 yeni maç" gibi

---

## ✅ SONUÇ

Observer Pattern ile **esnek, genişletilebilir ve bakımı kolay** bir bildirim sistemi oluşturduk.

**Avantajlar:**
- ✅ Gevşek bağlılık (Loose coupling)
- ✅ Kolay genişletilebilirlik
- ✅ SOLID prensiplere uygun
- ✅ Test edilebilir kod
- ✅ Gerçek dünya senaryolarına uygun

**FootBase Projesi İçin:**
- Kullanıcılar artık önemli olaylardan anında haberdar
- Admin ve editörler arasında sorunsuz iletişim
- Veritabanında kayıtlı bildirim geçmişi
- Genişletilebilir mimari (yeni bildirim tipleri eklenebilir)

---

**Geliştirici:** FootBase Takımı  
**Tarih:** Aralık 2025  
**Versiyon:** 1.0  
**Pattern:** Observer (Gözlemci) Design Pattern

