# BUILDER PATTERN - Maç Oluşturma (Classic GoF)

## 📚 Genel Bakış

Bu projede Builder Pattern'in **Classic GoF (Gang of Four)** implementasyonu kullanılmaktadır. Bu, Design Patterns kitabında tanımlanan orijinal ve akademik versiyondur.

Builder Pattern, karmaşık Mac nesnelerini adım adım oluşturmak için kullanılır. Mac entity'si çok sayıda parametreye sahip olduğu için Builder pattern ideal bir çözümdür.

## 🎯 Problem

Mac entity'si çok karmaşık:
- Ev sahibi takım (zorunlu)
- Deplasman takımı (zorunlu)
- Tarih (zorunlu)
- Saat (zorunlu)
- Stadyum (opsiyonel)
- Hakem (opsiyonel)
- Organizasyon (opsiyonel)
- Lig (opsiyonel)
- Skor bilgileri (opsiyonel)
- MacTakimlari ilişkileri

### Önce (Constructor Hell):

```java
// ❌ Okunması zor, parametrelerin sırası önemli, hata yapmak kolay
Mac mac = new Mac(
    galatasaray,  // ev sahibi
    fenerbahce,   // deplasman
    LocalDate.now(),
    LocalTime.of(20, 0),
    ttArena,
    hakem,
    superLig,
    null,  // organizasyon
    null,  // not
    0,     // ev sahibi skor
    0      // deplasman skor
);
```

### Sonra (Builder Pattern):

```java
// ✅ Okunabilir, esnek, test edilebilir
MacBuilderInterface builder = new StandardMacBuilder();
MacDirector director = new MacDirector(builder);

Mac mac = director.yaratLigMaci(
    galatasaray, fenerbahce,
    LocalDate.now(), LocalTime.of(20, 0)
);
```

## 📁 Dosya Yapısı

```
backend/src/main/java/com/footbase/patterns/builder/
├── MacBuilderInterface.java        # Builder interface (Product'ı oluşturan abstract interface)
├── StandardMacBuilder.java         # Concrete builder (Lig maçları için implementasyon)
├── MacDirector.java                # Director (Kompleks build senaryolarını yönetir)
└── README_BUILDER_PATTERN.md       # Bu dosya

backend/src/test/java/com/footbase/patterns/builder/
└── ClassicBuilderTest.java         # Unit testler
```

## 🏗️ Classic GoF Builder Pattern Yapısı

### UML Diyagram

```
                    ┌────────────────────────┐
                    │  MacBuilderInterface   │ ◄──── Builder (Interface)
                    │   <<interface>>        │
                    ├────────────────────────┤
                    │ + buildTakimlar()      │
                    │ + buildTarihSaat()     │
                    │ + buildStadyum()       │
                    │ + buildHakem()         │
                    │ + buildLig()           │
                    │ + buildOrganizasyon()  │
                    │ + buildSkorlar()       │
                    │ + buildNot()           │
                    │ + reset()              │
                    │ + getResult(): Mac     │
                    └──────────▲─────────────┘
                               │
                               │ implements
                               │
                    ┌──────────┴──────────────┐
                    │  StandardMacBuilder     │ ◄──── Concrete Builder
                    │   (Lig Maçları)         │
                    ├─────────────────────────┤
                    │ - mac: Mac              │
                    │ - evSahibiTakim: Takim  │
                    │ - deplasmanTakim: Takim │
                    ├─────────────────────────┤
                    │ + buildTakimlar()       │
                    │ + buildTarihSaat()      │
                    │ + getResult(): Mac      │
                    │ + reset()               │
                    └────────────▲────────────┘
                                 │
                                 │ uses
                                 │
                    ┌────────────┴──────────────┐
                    │      MacDirector          │ ◄──── Director
                    │  (Build Senaryoları)      │
                    ├───────────────────────────┤
                    │ - builder: Builder        │
                    ├───────────────────────────┤
                    │ + yaratLigMaci()          │
                    │ + yaratDetayliLigMaci()   │
                    │ + yaratTamKapsamliMac()   │
                    │ + yaratTamamlanmisMac()   │
                    │ + yaratDerbiMaci()        │
                    │ + yaratSampiyonlukMaci()  │
                    │ + yaratTestMaci()         │
                    └───────────────────────────┘
```

### Roller (GoF Pattern)

1. **Builder (MacBuilderInterface)**: Karmaşık nesnenin parçalarını oluşturmak için abstract interface
2. **Concrete Builder (StandardMacBuilder)**: Builder interface'ini implement eden, gerçek build işlemlerini yapan sınıf
3. **Director (MacDirector)**: Builder'ı kullanarak belirli senaryolara göre ürünler oluşturan sınıf
4. **Product (Mac)**: Oluşturulan karmaşık nesne

## 💻 Kullanım Örnekleri

### 1. Doğrudan Builder Kullanımı (Low Level)

```java
// Builder oluştur
MacBuilderInterface builder = new StandardMacBuilder();

// Adım adım build et
builder.buildTakimlar(galatasaray, fenerbahce);
builder.buildTarihSaat(LocalDate.now(), LocalTime.of(20, 0));
builder.buildStadyum(ttArena);
builder.buildHakem(hakem);
builder.buildLig(superLig);

// Sonuç al (getResult otomatik reset yapar)
Mac mac = builder.getResult();
```

### 2. Director ile Kullanım (Önerilen - High Level)

```java
// Builder ve Director oluştur
MacBuilderInterface builder = new StandardMacBuilder();
MacDirector director = new MacDirector(builder);

// Basit lig maçı (sadece zorunlu alanlar)
Mac ligMaci = director.yaratLigMaci(
    galatasaray, fenerbahce, 
    LocalDate.now(), LocalTime.of(20, 0)
);

// Detaylı lig maçı (stadyum + hakem + lig)
Mac detayliMac = director.yaratDetayliLigMaci(
    galatasaray, fenerbahce,
    LocalDate.now(), LocalTime.of(20, 0),
    ttArena, hakem, superLig
);

// Tam kapsamlı maç (tüm detaylar)
Mac tamKapsamliMac = director.yaratTamKapsamliMac(
    galatasaray, fenerbahce,
    LocalDate.now(), LocalTime.of(20, 0),
    ttArena, hakem, superLig, tff, "Özel maç"
);

// Derbi maçı (otomatik güvenlik notu)
Mac derbiMaci = director.yaratDerbiMaci(
    galatasaray, fenerbahce,
    LocalDate.now(), LocalTime.of(20, 0),
    ttArena, hakem
);
// Not: "⚡ DERBİ MAÇI - Yüksek güvenlik tedbirleri"

// Şampiyonluk maçı (otomatik önem notu)
Mac sampiyonlukMaci = director.yaratSampiyonlukMaci(
    galatasaray, fenerbahce,
    LocalDate.now(), LocalTime.of(20, 0),
    ttArena, hakem, superLig
);
// Not: "🏆 ŞAMPİYONLUK MAÇI - Kritik öneme sahip"

// Tamamlanmış maç (skorlu - geçmiş maç)
Mac tamamlanmisMac = director.yaratTamamlanmisMac(
    galatasaray, fenerbahce,
    LocalDate.of(2025, 12, 20), LocalTime.of(20, 0),
    2, 1  // Skor: 2-1
);
```

### 3. Service'te Kullanım (Production Code)

```java
@Service
public class MacOlusturmaService {
    
    private final MacDirector director;
    private final TakimRepository takimRepository;
    private final StadyumRepository stadyumRepository;
    
    public MacOlusturmaService(
            TakimRepository takimRepository,
            StadyumRepository stadyumRepository) {
        
        this.takimRepository = takimRepository;
        this.stadyumRepository = stadyumRepository;
        
        // Director'ı başlat
        MacBuilderInterface builder = new StandardMacBuilder();
        this.director = new MacDirector(builder);
    }
    
    /**
     * Editor tarafından hızlı maç oluşturma
     */
    public Mac editorMacOlustur(MacRequest request) {
        Takim evSahibi = takimRepository.findById(request.getEvSahibiId())
            .orElseThrow(() -> new RuntimeException("Ev sahibi takım bulunamadı"));
        
        Takim deplasman = takimRepository.findById(request.getDeplasmanId())
            .orElseThrow(() -> new RuntimeException("Deplasman takımı bulunamadı"));
        
        // Basit lig maçı oluştur
        return director.yaratLigMaci(
            evSahibi, deplasman,
            request.getTarih(), request.getSaat()
        );
    }
    
    /**
     * Admin tarafından detaylı maç oluşturma
     */
    public Mac adminDetayliMacOlustur(DetailedMacRequest request) {
        // Tüm ilişkili nesneleri getir
        Takim evSahibi = getTakim(request.getEvSahibiId());
        Takim deplasman = getTakim(request.getDeplasmanId());
        Stadyum stadyum = getStadyum(request.getStadyumId());
        Hakem hakem = getHakem(request.getHakemId());
        Lig lig = getLig(request.getLigId());
        
        // Detaylı lig maçı oluştur
        return director.yaratDetayliLigMaci(
            evSahibi, deplasman,
            request.getTarih(), request.getSaat(),
            stadyum, hakem, lig
        );
    }
    
    /**
     * Derbi maçı oluşturma (özel durum)
     */
    public Mac derbiMaciOlustur(MacRequest request) {
        Takim evSahibi = getTakim(request.getEvSahibiId());
        Takim deplasman = getTakim(request.getDeplasmanId());
        
        // Ev sahibi stadyumu otomatik getir
        Stadyum stadyum = stadyumRepository.findByTakim(evSahibi)
            .orElseThrow(() -> new RuntimeException("Stadyum bulunamadı"));
        
        // En iyi hakemi seç
        Hakem hakem = hakemService.enIyiHakemiBul();
        
        // Derbi maçı oluştur (otomatik güvenlik notu eklenir)
        return director.yaratDerbiMaci(
            evSahibi, deplasman,
            request.getTarih(), request.getSaat(),
            stadyum, hakem
        );
    }
}
```

### 4. Controller'da Kullanım

```java
@RestController
@RequestMapping("/api/editor")
public class EditorController {
    
    @Autowired
    private MacOlusturmaService macOlusturmaService;
    
    @PostMapping("/matches")
    public ResponseEntity<?> macOlustur(@RequestBody MacRequest request) {
        try {
            // Service'teki Director kullanımı
            Mac mac = macOlusturmaService.editorMacOlustur(request);
            
            // Veritabanına kaydet
            Mac kaydedilenMac = macRepository.save(mac);
            
            return ResponseEntity.ok(kaydedilenMac);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }
}
```

## 🎬 MacDirector Metodları

Director, farklı maç senaryoları için hazır metodlar sunar:

| Method | Parametreler | Açıklama | Otomatik Eklenenler |
|--------|--------------|----------|---------------------|
| `yaratLigMaci()` | Takımlar, tarih, saat | Basit lig maçı | - |
| `yaratDetayliLigMaci()` | + Stadyum, hakem, lig | Detaylı lig maçı | - |
| `yaratTamKapsamliMac()` | + Organizasyon, not | Tam kapsamlı maç | - |
| `yaratTamamlanmisMac()` | + Skorlar | Tamamlanmış maç | Skorlar |
| `yaratDerbiMaci()` | Takımlar, tarih, saat, stadyum, hakem | Derbi maçı | "⚡ DERBİ MAÇI" notu |
| `yaratSampiyonlukMaci()` | + Lig | Şampiyonluk maçı | "🏆 ŞAMPİYONLUK" notu |
| `yaratTestMaci()` | Takımlar, tarih, saat | Test maçı | "🧪 TEST MAÇI" notu |

## ⚠️ Validation Kuralları

Builder `getResult()` çağrıldığında şu kontrolleri yapar:

1. **Ev sahibi takım kontrolü**
   ```java
   if (evSahibiTakim == null) {
       throw new IllegalStateException("Ev sahibi takım zorunludur");
   }
   ```

2. **Deplasman takımı kontrolü**
   ```java
   if (deplasmanTakim == null) {
       throw new IllegalStateException("Deplasman takımı zorunludur");
   }
   ```

3. **Aynı takım kontrolü**
   ```java
   if (evSahibiTakim.getId().equals(deplasmanTakim.getId())) {
       throw new IllegalStateException("Aynı takım hem ev sahibi hem deplasman olamaz");
   }
   ```

4. **Tarih/saat kontrolü**
   ```java
   if (mac.getTarih() == null || mac.getSaat() == null) {
       throw new IllegalStateException("Tarih ve saat zorunludur");
   }
   ```

## ✨ Avantajlar

### 1. **Farklı Builder Implementasyonları**
```java
// Standard lig maçı builder
MacBuilderInterface standardBuilder = new StandardMacBuilder();

// Kupa maçı builder (gelecekte)
MacBuilderInterface kupaBuilder = new KupaMacBuilder();

// Uluslararası maç builder (gelecekte)
MacBuilderInterface uluslararasiBuilder = new UluslararasiMacBuilder();

// Aynı director, farklı builder'lar
director.setBuilder(kupaBuilder);
```

### 2. **Director ile Kompleks Senaryolar**
```java
// İş mantığı Director'da merkezi
Mac derbiMaci = director.yaratDerbiMaci(...);
// Otomatik olarak güvenlik notu ekler

Mac sampiyonlukMaci = director.yaratSampiyonlukMaci(...);
// Otomatik olarak önem notu ekler
```

### 3. **Test Edilebilirlik**
```java
// Mock builder inject edebilirsin
MacBuilderInterface mockBuilder = mock(MacBuilderInterface.class);
MacDirector director = new MacDirector(mockBuilder);

// Builder'ın metodlarını verify edebilirsin
verify(mockBuilder).buildTakimlar(any(), any());
verify(mockBuilder).buildTarihSaat(any(), any());
```

### 4. **Yeniden Kullanılabilirlik**
```java
// Aynı director ile birden fazla maç
Mac mac1 = director.yaratLigMaci(...);
Mac mac2 = director.yaratLigMaci(...);
Mac mac3 = director.yaratDerbiMaci(...);
```

### 5. **Separation of Concerns**
- **Builder**: Nesneyi nasıl oluşturacağını bilir
- **Director**: Hangi senaryolarda nasıl oluşturulacağını bilir
- **Client**: Sadece Director'ı kullanır, detayları bilmez

## 🎓 Design Principles (SOLID)

### 1. Single Responsibility Principle ✅
```java
// Builder: Sadece Mac oluşturmaktan sorumlu
StandardMacBuilder → Mac oluşturma

// Director: Sadece build senaryolarından sorumlu
MacDirector → Hangi Mac türü, nasıl oluşturulacak
```

### 2. Open/Closed Principle ✅
```java
// Yeni builder eklemek için mevcut kodu değiştirmeye gerek yok
class KupaMacBuilder implements MacBuilderInterface {
    // Yeni implementasyon
}

// Yeni director metodu eklemek kolay
public Mac yaratKupaMaci(...) {
    // Yeni senaryo
}
```

### 3. Liskov Substitution Principle ✅
```java
// Herhangi bir MacBuilderInterface kullanılabilir
MacBuilderInterface builder1 = new StandardMacBuilder();
MacBuilderInterface builder2 = new KupaMacBuilder();

// İkisi de aynı şekilde kullanılır
director.setBuilder(builder1);
director.setBuilder(builder2);
```

### 4. Interface Segregation Principle ✅
```java
// Interface sadece gerekli metodları içerir
// Gereksiz metodlar yok
```

### 5. Dependency Inversion Principle ✅
```java
// Director, concrete sınıfa değil interface'e bağımlı
public class MacDirector {
    private MacBuilderInterface builder;  // Interface'e bağımlı!
}
```

## 🧪 Test Örnekleri

```java
@Test
void testDirectorLigMaci() {
    // Given
    MacBuilderInterface builder = new StandardMacBuilder();
    MacDirector director = new MacDirector(builder);
    
    // When
    Mac mac = director.yaratLigMaci(
        galatasaray, fenerbahce,
        LocalDate.now(), LocalTime.of(20, 0)
    );
    
    // Then
    assertNotNull(mac);
    assertEquals(2, mac.getMacTakimlari().size());
}

@Test
void testDirectorDerbiMaci() {
    // When
    Mac derbiMaci = director.yaratDerbiMaci(
        galatasaray, fenerbahce,
        LocalDate.now(), LocalTime.of(20, 0),
        ttArena, hakem
    );
    
    // Then
    assertNotNull(derbiMaci);
    assertTrue(derbiMaci.getNot().contains("DERBİ"));
}
```

## 🚀 Sonuç

Classic GoF Builder Pattern sayesinde:
- ✅ Karmaşık Mac nesneleri sistematik şekilde oluşturuluyor
- ✅ Farklı builder implementasyonları eklenebilir
- ✅ Director ile kompleks senaryolar yönetiliyor
- ✅ Kod test edilebilir ve bakımı kolay
- ✅ SOLID prensipleri uygulanıyor
- ✅ GoF Design Patterns'e %100 uyumlu
- ✅ Akademik olarak eksiksiz implementasyon

**FootBase projesinde artık profesyonel ve akademik bir Builder Pattern implementasyonu var!** 🎉
