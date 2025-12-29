# 🎯 Command Pattern - FootBase Projesi

## 📋 İçindekiler

- [Genel Bakış](#genel-bakış)
- [Command Pattern Nedir?](#command-pattern-nedir)
- [Yapı](#yapı)
- [Kullanım Alanları](#kullanım-alanları)
- [Özellikler](#özellikler)
- [API Endpoint'leri](#api-endpointleri)
- [Kullanım Örnekleri](#kullanım-örnekleri)
- [Avantajlar](#avantajlar)
- [Test](#test)

---

## 🎯 Genel Bakış

FootBase projesinde **Command Pattern** kullanılarak **geri alınabilir (undo)** maç işlemleri sistemi geliştirildi.

### Özellikler

✅ **Skor Girişi** - Maçlara skor girişi yapma  
✅ **Maç Sonlandırma** - Maçı skorlarla birlikte sonlandırma  
✅ **Undo (Geri Alma)** - İşlemleri geri alabilme  
✅ **Redo (Tekrar Yapma)** - Geri alınan işlemleri tekrar yapabilme  
✅ **İşlem Geçmişi** - Tüm işlemlerin kaydı  
✅ **Kullanıcı Bazlı Kontrol** - Her kullanıcı sadece kendi işlemlerini geri alabilir

---

## 📚 Command Pattern Nedir?

**Command Pattern**, Gang of Four (GoF) tasarım kalıplarından biridir ve **Behavioral (Davranışsal)** kategorisindedir.

### Amaç

İstekleri nesneler olarak kapsüllemek, böylece:
- İstemcileri farklı isteklerle parametrize edebilme
- İstekleri kuyruğa alabilme veya loglayabilme
- Geri alınabilir işlemler yapabilme

### Yapı

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ creates
       ▼
┌─────────────┐      executes      ┌──────────────┐
│   Invoker   │ ───────────────> │   Command    │ (interface)
└─────────────┘                    └──────┬───────┘
                                          │ implements
                                          ▼
                              ┌──────────────────────┐
                              │  ConcreteCommand     │
                              │  - execute()         │
                              │  - undo()            │
                              │  - redo()            │
                              └──────────────────────┘
                                        │ uses
                                        ▼
                              ┌──────────────────────┐
                              │     Receiver         │
                              │  (MacRepository)     │
                              └──────────────────────┘
```

---

## 🏗️ Yapı

### 1. Command Interface

```java
public interface Command {
    boolean execute();
    boolean undo();
    boolean redo();
    String getDescription();
    String getCommandType();
    Long getKullaniciId();
    LocalDateTime getExecutionTime();
}
```

### 2. Abstract Base Command

```java
public abstract class MacCommand implements Command {
    protected abstract boolean doExecute();
    protected abstract boolean doUndo();
    // Ortak işlevsellik (logging, hata yönetimi)
}
```

### 3. Concrete Commands

#### SkorGirisiCommand

Maçlara skor girişi yapar ve geri alınabilir.

**Özellikler:**
- Ev sahibi ve deplasman skorlarını günceller
- Önceki skorları saklar (undo için)
- İşlem loglanır

```java
public class SkorGirisiCommand extends MacCommand {
    private final SkorGirisiDTO skorGirisiDTO;
    private Map<Long, Integer> oncekiSkorlar; // Undo için
    
    @Override
    protected boolean doExecute() {
        // Skorları güncelle
    }
    
    @Override
    protected boolean doUndo() {
        // Önceki skorları geri yükle
    }
}
```

#### MacSonlandirCommand

Maçı sonlandırır (skorları girer ve durumu günceller).

**Özellikler:**
- Skorları günceller
- Maç durumunu "BITTI" olarak işaretler
- Sonucu hesaplar (Ev Sahibi Galip / Deplasman Galip / Beraberlik)
- Geri alınabilir

```java
public class MacSonlandirCommand extends MacCommand {
    private final MacSonlandirDTO macSonlandirDTO;
    private Map<Long, Integer> oncekiSkorlar;
    private String oncekiDurum;
    
    @Override
    protected boolean doExecute() {
        // Skorları güncelle ve maçı sonlandır
    }
    
    @Override
    protected boolean doUndo() {
        // Önceki duruma döndür
    }
}
```

### 4. Command Invoker

Komutları çalıştırır ve geçmişe kaydeder.

```java
@Component
public class CommandInvoker {
    @Autowired
    private CommandHistory commandHistory;
    
    public boolean executeCommand(Command command) {
        boolean result = command.execute();
        if (result) {
            commandHistory.push(command);
        }
        return result;
    }
    
    public boolean undo() {
        return commandHistory.undo();
    }
    
    public boolean redo() {
        return commandHistory.redo();
    }
}
```

### 5. Command History

İşlem geçmişini tutar ve undo/redo sağlar.

```java
@Component
public class CommandHistory {
    private final Stack<Command> history = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    
    public void push(Command command) { ... }
    public boolean undo() { ... }
    public boolean redo() { ... }
    public boolean undoByKullaniciId(Long kullaniciId) { ... }
}
```

### 6. Service Layer

```java
@Service
public class MacCommandService {
    @Autowired
    private CommandInvoker commandInvoker;
    
    public Map<String, Object> skorGirisiYap(...) { ... }
    public Map<String, Object> macSonlandir(...) { ... }
    public Map<String, Object> sonIslemGeriAl(...) { ... }
    public Map<String, Object> islemGecmisiGetir() { ... }
}
```

---

## 🎯 Kullanım Alanları

### 1. Admin Paneli

Admin kullanıcıları onaylanan maçlar için:
- ✅ Skor girişi yapabilir
- ✅ Maçı sonlandırabilir
- ✅ Yanlış işlemleri geri alabilir (undo)
- ✅ İşlem geçmişini görüntüleyebilir

### 2. Editor Paneli

Editor kullanıcıları oluşturdukları maçlar için:
- ✅ Skor girişi yapabilir
- ✅ Maçı sonlandırabilir
- ✅ Yanlış işlemleri geri alabilir (undo)
- ✅ İşlem geçmişini görüntüleyebilir

---

## 📡 API Endpoint'leri

### Admin Endpoint'leri

#### 1. Skor Girişi

```http
POST /api/admin/matches/score
Authorization: Bearer {token}
Content-Type: application/json

{
  "macId": 1,
  "evSahibiSkor": 3,
  "deplasmanSkor": 1,
  "aciklama": "İlk yarı sonu"
}
```

**Yanıt:**
```json
{
  "basarili": true,
  "mesaj": "Skor girişi başarılı!",
  "macId": 1,
  "evSahibiSkor": 3,
  "deplasmanSkor": 1,
  "gecmisBoyutu": 1
}
```

#### 2. Maç Sonlandırma

```http
POST /api/admin/matches/finish
Authorization: Bearer {token}
Content-Type: application/json

{
  "macId": 1,
  "evSahibiSkor": 3,
  "deplasmanSkor": 2,
  "durum": "BITTI",
  "aciklama": "Maç sona erdi"
}
```

**Yanıt:**
```json
{
  "basarili": true,
  "mesaj": "Maç başarıyla sonlandırıldı!",
  "macId": 1,
  "evSahibiSkor": 3,
  "deplasmanSkor": 2,
  "durum": "BITTI",
  "sonuc": "EV_SAHIBI_GALIP",
  "gecmisBoyutu": 2
}
```

#### 3. Son İşlemi Geri Al (Undo)

```http
POST /api/admin/matches/undo
Authorization: Bearer {token}
```

**Yanıt:**
```json
{
  "basarili": true,
  "mesaj": "İşlem başarıyla geri alındı!",
  "geriAlinanIslem": "Maç Sonlandırma: Maç #1 - 3:2 (BITTI) - (Kullanıcı: 5)",
  "islemTipi": "MAC_SONLANDIR"
}
```

#### 4. İşlem Geçmişini Getir

```http
GET /api/admin/matches/history
Authorization: Bearer {token}
```

**Yanıt:**
```json
{
  "gecmis": [
    {
      "tip": "SKOR_GIRISI",
      "aciklama": "Skor Girişi: Maç #1 - 3:1 (Kullanıcı: 5)",
      "kullaniciId": 5,
      "zaman": "2024-12-26T15:30:00"
    },
    {
      "tip": "MAC_SONLANDIR",
      "aciklama": "Maç Sonlandırma: Maç #1 - 3:2 (BITTI) - (Kullanıcı: 5)",
      "kullaniciId": 5,
      "zaman": "2024-12-26T17:15:00"
    }
  ],
  "toplamIslem": 2,
  "redoMevcutMu": false
}
```

### Editor Endpoint'leri

Editor için aynı endpoint'ler `/api/editor` prefix'i ile mevcuttur:

- `POST /api/editor/matches/score-command`
- `POST /api/editor/matches/finish-command`
- `POST /api/editor/matches/undo`
- `GET /api/editor/matches/history`

---

## 💡 Kullanım Örnekleri

### Örnek 1: Skor Girişi ve Geri Alma

```javascript
// 1. Skor girişi yap
const skorGirisiResponse = await fetch('/api/admin/matches/score', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    macId: 1,
    evSahibiSkor: 2,
    deplasmanSkor: 0
  })
});

// 2. Yanıt
{
  "basarili": true,
  "mesaj": "Skor girişi başarılı!",
  "evSahibiSkor": 2,
  "deplasmanSkor": 0
}

// 3. Yanlış yaptın, geri al!
const undoResponse = await fetch('/api/admin/matches/undo', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

// 4. Geri alındı
{
  "basarili": true,
  "mesaj": "İşlem başarıyla geri alındı!"
}
```

### Örnek 2: Maç Sonlandırma

```javascript
// Maçı sonlandır
const response = await fetch('/api/admin/matches/finish', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    macId: 1,
    evSahibiSkor: 3,
    deplasmanSkor: 1,
    durum: "BITTI"
  })
});

// Yanıt
{
  "basarili": true,
  "sonuc": "EV_SAHIBI_GALIP",
  "evSahibiSkor": 3,
  "deplasmanSkor": 1
}
```

---

## ✨ Avantajlar

### 1. Geri Alınabilir İşlemler (Undo)

```
İşlem Yapıldı → Hata Fark Edildi → UNDO! → Önceki Durum
```

**Örnek Senaryo:**
- Admin yanlışlıkla 3-1 yerine 3-0 girer
- "Undo" butonuna basar
- Sistem önceki skora döner
- Doğru skoru tekrar girer

### 2. İşlem Geçmişi

Tüm işlemler kaydedilir:
- Kim yaptı?
- Ne zaman yaptı?
- Ne yaptı?
- Geri alındı mı?

### 3. Kullanıcı Bazlı Kontrol

Her kullanıcı **sadece kendi işlemlerini** geri alabilir:

```java
public boolean undoByKullaniciId(Long kullaniciId) {
    Command lastCommand = history.peek();
    if (!lastCommand.getKullaniciId().equals(kullaniciId)) {
        return false; // Bu işlem size ait değil!
    }
    return undo();
}
```

### 4. Loosely Coupled (Gevşek Bağlı)

Controller → Service → Invoker → Command → Repository

Her katman bağımsız, test edilebilir.

### 5. Genişletilebilirlik

Yeni komut eklemek kolay:

```java
public class MacIptalCommand extends MacCommand {
    @Override
    protected boolean doExecute() {
        // Maçı iptal et
    }
    
    @Override
    protected boolean doUndo() {
        // İptali geri al
    }
}
```

### 6. Transaction Yönetimi

Her komut bir transaction içinde çalışır:

```java
@Transactional
public Map<String, Object> skorGirisiYap(...) {
    // Tüm işlemler atomik
}
```

---

## 🧪 Test

### Backend'i Başlat

```bash
cd backend
./mvnw spring-boot:run
```

### Log Çıktısı

```
INFO - 🎯 SKOR_GIRISI çalıştırılıyor... (Kullanıcı: 5)
INFO - 📊 Ev sahibi skor güncellendi: 0 → 3
INFO - 📊 Deplasman skor güncellendi: 0 → 1
INFO - ⚽ Skor girişi tamamlandı: 3 - 1
INFO - ✅ SKOR_GIRISI başarılı!
INFO - 📝 Komut geçmişe eklendi: SKOR_GIRISI (Toplam: 1)
```

### Undo Test

```
INFO - 🔄 Kullanıcı #5 için son komut geri alınıyor...
INFO - 🔄 SKOR_GIRISI geri alınıyor...
INFO - 🔄 Skor geri alındı: 0 (ID: 1)
INFO - 🔄 Skor geri alındı: 0 (ID: 2)
INFO - ✅ Skor girişi başarıyla geri alındı!
INFO - ✅ SKOR_GIRISI geri alındı!
```

### Postman ile Test

1. **Login** → Token al
2. **POST** `/api/admin/matches/score` → Skor gir
3. **POST** `/api/admin/matches/undo` → Geri al
4. **GET** `/api/admin/matches/history` → Geçmişi gör

---

## 🎓 Design Pattern Özeti

| Özellik | Açıklama |
|---------|----------|
| **Pattern Tipi** | Behavioral (Davranışsal) |
| **Gang of Four** | ✅ Classic GoF Pattern |
| **Amaç** | İstekleri nesneler olarak kapsüllemek |
| **Ana Özellik** | Geri alınabilir işlemler (Undo/Redo) |
| **Karmaşıklık** | Orta |
| **Kullanım Alanı** | Transaction yönetimi, History tracking |

---

## 📊 Sınıf Diyagramı

```
┌──────────────────────┐
│     Command          │ (interface)
├──────────────────────┤
│ + execute(): boolean │
│ + undo(): boolean    │
│ + redo(): boolean    │
└──────────┬───────────┘
           │ implements
           ▼
┌──────────────────────┐
│    MacCommand        │ (abstract)
├──────────────────────┤
│ # doExecute()        │
│ # doUndo()           │
└──────────┬───────────┘
           │ extends
           ▼
┌──────────────────────────────┐
│  SkorGirisiCommand           │
│  MacSonlandirCommand         │
└──────────────────────────────┘

┌──────────────────────┐
│  CommandInvoker      │
├──────────────────────┤
│ - commandHistory     │
├──────────────────────┤
│ + executeCommand()   │
│ + undo()             │
│ + redo()             │
└──────────────────────┘

┌──────────────────────┐
│  CommandHistory      │
├──────────────────────┤
│ - history: Stack     │
│ - redoStack: Stack   │
├──────────────────────┤
│ + push()             │
│ + undo()             │
│ + redo()             │
└──────────────────────┘
```

---

## 🚀 Gelecek Geliştirmeler

1. **Redo Özelliği** - Geri alınan işlemleri tekrar yapma
2. **Macro Commands** - Birden fazla komutu grupla
3. **Scheduled Undo** - Zamanlı geri alma
4. **Command Queue** - Asenkron komut işleme
5. **Persistent History** - Veritabanında geçmiş tutma

---

## 📚 Kaynaklar

- **Gang of Four (GoF)** - Design Patterns: Elements of Reusable Object-Oriented Software
- **Head First Design Patterns** - Command Pattern
- **Refactoring.Guru** - [Command Pattern](https://refactoring.guru/design-patterns/command)

---

## 🎯 Sonuç

Command Pattern ile FootBase projesine:
- ✅ Geri alınabilir işlemler eklendi
- ✅ İşlem geçmişi tutuldu
- ✅ Kod daha esnek ve genişletilebilir hale geldi
- ✅ Hata yönetimi iyileştirildi
- ✅ Transaction yönetimi güçlendirildi

**🎨 Command Pattern - İşlemleri Nesneler Olarak Kapsülle!**

---

**Güncellenme Tarihi:** 26 Aralık 2024  
**Versiyon:** 1.0.0  
**Durum:** ✅ Aktif ve Kullanımda


