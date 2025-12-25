# Editör-Admin Maç Onay Sistemi

Bu sistem, Observer Design Pattern kullanılarak oluşturulmuştur. Editörler maç ekler, admin'ler onaylar.

## Yapı

### Backend

#### Observer Pattern Implementation
- `patterns/observer/Subject.java` - Observer pattern için temel arayüz
- `patterns/observer/Observer.java` - Observer arayüzü
- `patterns/observer/MacApprovalSubject.java` - Maç onay işlemlerini yöneten subject
- `patterns/observer/AdminObserver.java` - Admin bildirimlerini işleyen observer

#### Entity Değişiklikleri
- `Mac.java` - `onayDurumu`, `editor`, `editorId` alanları eklendi (@Transient)
  - `onayDurumu`: "ONAY_BEKLIYOR", "YAYINDA", "REDDEDILDI"
  - `editor`: Maçı ekleyen editör
  - `editorId`: Editör ID'si

#### Repository
- `EditorYoneticileriRepository.java` - Editor-Admin ilişkilerini yönetir

#### Service
- `MacService.java` - Yeni metodlar eklendi:
  - `editorMacOlustur()` - Editör tarafından maç oluşturma (observer ile bildirim)
  - `onayBekleyenMaclariGetir()` - Onay bekleyen maçları getir
  - `adminOnayBekleyenMaclariGetir()` - Belirli admin'in onay bekleyen maçları
  - `macOnayla()` - Maçı onayla (observer ile bildirim)
  - `macReddet()` - Maçı reddet (observer ile bildirim)
  - `yayindakiMaclariGetir()` - Yayında olan maçları getir

#### Controller'lar
- `EditorController.java` - Editör için endpoint'ler:
  - `POST /api/editor/matches` - Maç ekle

- `AdminController.java` - Admin için yeni endpoint'ler:
  - `GET /api/admin/matches/pending` - Onay bekleyen maçları getir
  - `POST /api/admin/matches/{macId}/approve` - Maçı onayla
  - `POST /api/admin/matches/{macId}/reject` - Maçı reddet

### Frontend

#### Sayfalar
- `EditorMatchAddPage.js` - Editör maç ekleme sayfası
  - Route: `/app/editor/match/add`
  - Takımları seçme, tarih/saat girme

- `AdminMatchApprovalPage.js` - Admin maç onaylama sayfası
  - Route: `/app/admin/matches/approval`
  - Onay bekleyen maçları listeleme, onaylama/reddetme

#### API
- `utils/api.js` - Yeni API fonksiyonları:
  - `editorAPI.createMatch()` - Maç oluştur
  - `adminAPI.getPendingMatches()` - Onay bekleyen maçları getir
  - `adminAPI.approveMatch()` - Maçı onayla
  - `adminAPI.rejectMatch()` - Maçı reddet

## Akış

1. **Editör Maç Ekler:**
   - Editör `/app/editor/match/add` sayfasından maç ekler
   - Backend'de `editorMacOlustur()` çağrılır
   - Maç `ONAY_BEKLIYOR` durumunda kaydedilir
   - Observer pattern ile editörün admin'ine bildirim gönderilir

2. **Admin Onay Bekleyen Maçları Görür:**
   - Admin `/app/admin/matches/approval` sayfasına gider
   - Sadece kendi editörlerinin eklediği maçları görür

3. **Admin Maçı Onaylar/Reddeder:**
   - Admin maçı onaylarsa `YAYINDA` durumuna geçer
   - Admin maçı reddederse `REDDEDILDI` durumuna geçer
   - Observer pattern ile bildirim gönderilir

4. **Yayın:**
   - Sadece `YAYINDA` durumundaki maçlar normal kullanıcılara gösterilir
   - `yayindakiMaclariGetir()` metodu ile filtrelenir

## Observer Pattern Kullanımı

```java
// Maç eklendiğinde
MacApprovalSubject subject = new MacApprovalSubject();
AdminObserver adminObserver = new AdminObserver(admin);
subject.attach(adminObserver);
subject.macEklendi(mac);
```

## Notlar

- `Mac` entity'sindeki `onayDurumu`, `editor`, `editorId` alanları `@Transient` olarak işaretlendi (veritabanında kolon yok)
- Gelecekte bu alanları veritabanına eklemek için migration gerekebilir
- Observer pattern sayesinde bildirim sistemi kolayca genişletilebilir (email, push notification vb.)

