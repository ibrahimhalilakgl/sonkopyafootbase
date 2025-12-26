# ⛓️ Chain of Responsibility Pattern

## 📌 Ne İşe Yarar?

Chain of Responsibility Pattern, bir isteği işleyecek nesneyi runtime'da belirler. İsteği alan nesneler zincir halinde bağlanır ve her biri isteği ya işler ya da zincirdeki bir sonraki nesneye iletir.

## 🎯 Projede Kullanımı

FootBase projesinde **2 farklı zincir** uygulandı:

### 1️⃣ Yorum Moderasyon Zinciri 💬

```
Yorum → Küfür Filtresi → Spam Kontrolü → Uzunluk Kontrolü → Link Kontrolü → ✅
```

### 2️⃣ Maç Onay Zinciri ⚽

```
Maç → Tarih Kontrolü → Takım Kontrolü → Saat Kontrolü → Stadyum Kontrolü → ✅
```

## 📋 Pattern Yapısı

```
Handler<T> (Abstract)
├── YorumHandler
│   ├── KufurFiltresiHandler
│   ├── SpamKontrolHandler
│   ├── UzunlukKontrolHandler
│   └── LinkKontrolHandler
│
└── MacOnayHandler
    ├── TarihKontrolHandler
    ├── TakimKontrolHandler
    ├── SaatKontrolHandler
    └── StadyumKontrolHandler

Chain Managers:
├── YorumModerationChain
└── MacOnayChain
```

## 💻 Kullanım Örnekleri

### 1️⃣ Yorum Moderasyonu

```java
@Autowired
private YorumModerationChain yorumChain;

public void yorumEkle(Yorum yorum) {
    // Moderasyon zincirinden geçir
    HandlerResult result = yorumChain.moderate(yorum);
    
    if (result.isSuccess()) {
        // Yorum onaylandı, kaydet
        yorumRepository.save(yorum);
        System.out.println("✅ Yorum başarıyla eklendi");
    } else {
        // Moderasyondan geçemedi
        System.out.println("❌ " + result.getMessage());
    }
}
```

**Çıktı:**
```
═══════════════════════════════════════════════════════
💬 YORUM MODERASYONU BAŞLIYOR
═══════════════════════════════════════════════════════
Yorum: "Harika bir maçtı! Galatasaray çok iyiydi."
Kullanıcı: ahmet@example.com
───────────────────────────────────────────────────────
🔗 [KufurFiltresiHandler] işleniyor...
✅ [KufurFiltresiHandler] başarılı
🔗 [SpamKontrolHandler] işleniyor...
✅ [SpamKontrolHandler] başarılı
🔗 [UzunlukKontrolHandler] işleniyor...
✅ [UzunlukKontrolHandler] başarılı
🔗 [LinkKontrolHandler] işleniyor...
✅ [LinkKontrolHandler] başarılı
───────────────────────────────────────────────────────
✅ MODERASYON BAŞARILI - Yorum onaylandı
═══════════════════════════════════════════════════════
```

### 2️⃣ Yorum Moderasyonu - Başarısız

```java
Yorum yorum = new Yorum();
yorum.setMesaj("Bu maç çok kötüydü aptal hakemler!");
yorum.setKullanici(kullanici);

HandlerResult result = yorumChain.moderate(yorum);
// Result: success=false, message="Yorumunuz uygunsuz içerik barındırıyor"
```

**Çıktı:**
```
═══════════════════════════════════════════════════════
💬 YORUM MODERASYONU BAŞLIYOR
═══════════════════════════════════════════════════════
Yorum: "Bu maç çok kötüydü aptal hakemler!"
───────────────────────────────────────────────────────
🔗 [KufurFiltresiHandler] işleniyor...
❌ [KufurFiltresiHandler] başarısız: Yorumunuz uygunsuz içerik barındırıyor
───────────────────────────────────────────────────────
❌ MODERASYON BAŞARISIZ - Yorumunuz uygunsuz içerik barındırıyor
═══════════════════════════════════════════════════════
```

### 3️⃣ Maç Onay Süreci

```java
@Autowired
private MacOnayChain macChain;

public void macOlustur(Mac mac) {
    // Onay zincirinden geçir
    HandlerResult result = macChain.validate(mac);
    
    if (result.isSuccess()) {
        // Maç onaylandı, kaydet
        macRepository.save(mac);
        System.out.println("✅ Maç başarıyla oluşturuldu");
    } else {
        // Onaydan geçemedi
        System.out.println("❌ " + result.getMessage());
    }
}
```

**Çıktı:**
```
═══════════════════════════════════════════════════════
⚽ MAÇ ONAY SÜRECİ BAŞLIYOR
═══════════════════════════════════════════════════════
Tarih: 2024-12-30 20:00
Stadyum: Türk Telekom Stadyumu
───────────────────────────────────────────────────────
🔗 [TarihKontrolHandler] işleniyor...
✅ [TarihKontrolHandler] başarılı
🔗 [TakimKontrolHandler] işleniyor...
✅ [TakimKontrolHandler] başarılı
🔗 [SaatKontrolHandler] işleniyor...
✅ [SaatKontrolHandler] başarılı
🔗 [StadyumKontrolHandler] işleniyor...
✅ [StadyumKontrolHandler] başarılı
───────────────────────────────────────────────────────
✅ ONAY BAŞARILI - Maç onaylandı
═══════════════════════════════════════════════════════
```

### 4️⃣ Maç Onay - Başarısız

```java
Mac mac = new Mac();
mac.setTarih(LocalDate.now().minusDays(5)); // Geçmiş tarih
mac.setSaat(LocalTime.of(20, 0));

HandlerResult result = macChain.validate(mac);
// Result: success=false, message="Maç tarihi geçmişte olamaz"
```

**Çıktı:**
```
═══════════════════════════════════════════════════════
⚽ MAÇ ONAY SÜRECİ BAŞLIYOR
═══════════════════════════════════════════════════════
Tarih: 2024-12-20 20:00
───────────────────────────────────────────────────────
🔗 [TarihKontrolHandler] işleniyor...
❌ [TarihKontrolHandler] başarısız: Maç tarihi geçmişte olamaz
───────────────────────────────────────────────────────
❌ ONAY BAŞARISIZ - Maç tarihi geçmişte olamaz
═══════════════════════════════════════════════════════
```

### 5️⃣ Hızlı Kontrol (Log'suz)

```java
// Sadece true/false döndürür, log atmaz
boolean yorumGecerli = yorumChain.quickCheck(yorum);
boolean macGecerli = macChain.quickValidate(mac);
```

### 6️⃣ Zincir Görselleştirme

```java
String yorumZinciri = yorumChain.getChainVisualization();
// "1. KufurFiltresiHandler → 2. SpamKontrolHandler → 3. UzunlukKontrolHandler → 4. LinkKontrolHandler"

String macZinciri = macChain.getChainVisualization();
// "1. TarihKontrolHandler → 2. TakimKontrolHandler → 3. SaatKontrolHandler → 4. StadyumKontrolHandler"
```

## 🔑 Anahtar Kavramlar

### Handler (Abstract Class)
```java
public abstract class Handler<T> {
    protected Handler<T> next;
    
    public Handler<T> setNext(Handler<T> next) {
        this.next = next;
        return next; // Method chaining
    }
    
    public final HandlerResult handle(T request) {
        HandlerResult result = doHandle(request);
        
        if (!result.isSuccess()) {
            return result; // Zinciri kes
        }
        
        if (next != null) {
            return next.handle(request); // Devam et
        }
        
        return HandlerResult.success();
    }
    
    protected abstract HandlerResult doHandle(T request);
}
```

### Concrete Handler
```java
@Component
public class KufurFiltresiHandler extends YorumHandler {
    @Override
    protected HandlerResult doHandle(Yorum yorum) {
        // Küfür kontrolü yap
        if (kufurVar(yorum.getMesaj())) {
            return HandlerResult.failure("Küfür tespit edildi");
        }
        return HandlerResult.success();
    }
}
```

### Chain Manager
```java
@Component
public class YorumModerationChain {
    @Autowired
    private KufurFiltresiHandler kufurFiltresi;
    
    @Autowired
    private SpamKontrolHandler spamKontrol;
    
    private YorumHandler chain;
    
    @PostConstruct
    public void buildChain() {
        // Zinciri kur
        kufurFiltresi.setNext(spamKontrol)
                     .setNext(uzunlukKontrol)
                     .setNext(linkKontrol);
        
        chain = kufurFiltresi;
    }
    
    public HandlerResult moderate(Yorum yorum) {
        return chain.handle(yorum);
    }
}
```

## 📊 Avantajlar

| Avantaj | Açıklama |
|---------|----------|
| ✅ **Loose Coupling** | Gönderici ve alıcı bağımsız |
| ✅ **Esneklik** | Zincir runtime'da değiştirilebilir |
| ✅ **Single Responsibility** | Her handler tek sorumluluk |
| ✅ **Genişletilebilir** | Yeni handler eklemek kolay |
| ✅ **Sıralama** | İşlem sırası kolayca değiştirilebilir |

## 🎭 Diğer Pattern'lerle Karşılaştırma

| Pattern | Ne Zaman Kullan |
|---------|-----------------|
| **Chain of Responsibility** | Birden fazla nesne isteği işleyebilir |
| **Decorator** | Nesneye dinamik özellik ekle |
| **Strategy** | Algoritma değiştirilebilir olmalı |
| **Template Method** | Algoritma iskeleti sabit |

## 🧪 Test Örnekleri

### Test 1: Yorum Moderasyonu

```java
@Test
public void testYorumKabulEdilir() {
    Yorum yorum = new Yorum();
    yorum.setMesaj("Harika bir maçtı!");
    yorum.setKullanici(kullanici);
    
    HandlerResult result = yorumChain.moderate(yorum);
    
    assertTrue(result.isSuccess());
}

@Test
public void testKufurEngellenir() {
    Yorum yorum = new Yorum();
    yorum.setMesaj("Bu maç çok kötüydü aptal!");
    
    HandlerResult result = yorumChain.moderate(yorum);
    
    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().contains("uygunsuz içerik"));
}

@Test
public void testCokKisaYorumEngellenir() {
    Yorum yorum = new Yorum();
    yorum.setMesaj("OK");
    
    HandlerResult result = yorumChain.moderate(yorum);
    
    assertFalse(result.isSuccess());
}
```

### Test 2: Maç Onayı

```java
@Test
public void testMacKabulEdilir() {
    Mac mac = new Mac();
    mac.setTarih(LocalDate.now().plusDays(7));
    mac.setSaat(LocalTime.of(20, 0));
    mac.setStadyum("Türk Telekom Stadyumu");
    // Takımları ekle...
    
    HandlerResult result = macChain.validate(mac);
    
    assertTrue(result.isSuccess());
}

@Test
public void testGecmisTarihEngellenir() {
    Mac mac = new Mac();
    mac.setTarih(LocalDate.now().minusDays(5));
    mac.setSaat(LocalTime.of(20, 0));
    
    HandlerResult result = macChain.validate(mac);
    
    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().contains("geçmişte"));
}
```

## 🚀 Gerçek Kullanım - Service Entegrasyonu

### YorumService'te Kullanım

```java
@Service
public class YorumService {
    
    @Autowired
    private YorumModerationChain moderationChain;
    
    @Autowired
    private YorumRepository yorumRepository;
    
    public Yorum yorumOlustur(Long macId, Long kullaniciId, String mesaj) {
        Yorum yorum = new Yorum();
        yorum.setMesaj(mesaj);
        yorum.setKullanici(kullaniciRepository.findById(kullaniciId).orElseThrow());
        yorum.setMac(macRepository.findById(macId).orElseThrow());
        
        // Moderasyon zincirinden geçir
        HandlerResult result = moderationChain.moderate(yorum);
        
        if (!result.isSuccess()) {
            throw new RuntimeException("Yorum moderasyondan geçemedi: " + result.getMessage());
        }
        
        // Onaylandı, kaydet
        return yorumRepository.save(yorum);
    }
}
```

### EditorController'da Kullanım

```java
@RestController
@RequestMapping("/api/editor/matches")
public class EditorController {
    
    @Autowired
    private MacOnayChain onayChain;
    
    @Autowired
    private MacRepository macRepository;
    
    @PostMapping
    public ResponseEntity<?> macOlustur(@RequestBody Mac mac) {
        // Onay zincirinden geçir
        HandlerResult result = onayChain.validate(mac);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest()
                .body(Map.of("hata", result.getMessage()));
        }
        
        // Admin onayı için beklet
        mac.setOnayDurumu("ONAY_BEKLIYOR");
        Mac kaydedilen = macRepository.save(mac);
        
        return ResponseEntity.ok(kaydedilen);
    }
}
```

## 📝 Notlar

- ✅ Her handler **@Component** olmalı (Spring DI için)
- ✅ Zincir **@PostConstruct** ile kurulmalı
- ✅ Handler'lar **öncelik** sırasına göre çalışır
- ✅ Bir handler başarısız olursa **zincir kesilir**
- ✅ **HandlerResult** ile sonuç döndürülür

## 🎯 Kullanım Alanları

### Mevcut Kullanım

1. **Yorum Moderasyonu:**
   - Küfür filtresi
   - Spam kontrolü
   - Uzunluk kontrolü
   - Link kontrolü

2. **Maç Onay Süreci:**
   - Tarih kontrolü
   - Takım kontrolü
   - Saat kontrolü
   - Stadyum kontrolü

### Gelecekte Eklenebilir

3. **Kullanıcı Kayıt Onayı:**
   - Email validation
   - Şifre gücü kontrolü
   - Spam bot kontrolü
   - Captcha doğrulama

4. **İstek Filtreleme:**
   - Authentication
   - Authorization
   - Rate limiting
   - Logging

## 🎯 Sonuç

Chain of Responsibility Pattern sayesinde:
- ✅ Yorumlar otomatik moderasyondan geçiyor
- ✅ Maçlar otomatik validasyondan geçiyor
- ✅ Yeni kontrol eklemek çok kolay
- ✅ Kontrol sırası kolayca değiştirilebilir
- ✅ Her kontrol bağımsız test edilebilir

**⛓️ Chain of Responsibility - İsteği Zincirleme İşle!**

