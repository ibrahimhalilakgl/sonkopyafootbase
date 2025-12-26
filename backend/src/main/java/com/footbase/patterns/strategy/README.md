# 🎯 Strategy Design Pattern

## 📌 Ne İşe Yarar?

Strategy Pattern, bir algoritma ailesini tanımlar, her birini kapsüller ve birbirinin yerine kullanılabilir yapar. Strategy, algoritmanın kullanılan istemciden bağımsız olarak değişmesini sağlar.

## 🎯 Projede Kullanımı

**Değerlendirme Hesaplama Sistemi** için kullanılıyor:

Farklı kullanıcı tipleri değerlendirmelerini farklı ağırlıklarla verirler:

| Kullanıcı Tipi | Ağırlık | Örnek |
|----------------|---------|-------|
| 👑 **Admin** | **3x** | 5 yıldız → 15 puan |
| ✏️ **Editör** | **2x** | 5 yıldız → 10 puan |
| 👤 **Normal** | **1x** | 5 yıldız → 5 puan |

## 📋 Pattern Yapısı

```
DegerlendirmeStrateji (Interface)
├── AdminDegerlendirmeStrateji (3x)
├── EditorDegerlendirmeStrateji (2x)
└── NormalKullaniciDegerlendirmeStrateji (1x)

DegerlendirmeContext (Context)
└── Stratejileri kullanır

DegerlendirmeStratejiFactory (Factory)
└── Strateji seçimi yapar
```

## 💻 Kullanım Örnekleri

### 1️⃣ Temel Kullanım - Context ile

```java
@Autowired
private DegerlendirmeContext context;

public void degerlendirmeYap() {
    // Admin değerlendirmesi
    context.stratejiSec("ADMIN");
    double adminPuan = context.puanHesapla(5);
    // Sonuç: 15.0 (5 × 3)
    
    // Editör değerlendirmesi
    context.stratejiSec("EDITOR");
    double editorPuan = context.puanHesapla(5);
    // Sonuç: 10.0 (5 × 2)
    
    // Normal kullanıcı değerlendirmesi
    context.stratejiSec("USER");
    double normalPuan = context.puanHesapla(5);
    // Sonuç: 5.0 (5 × 1)
}
```

**Çıktı:**
```
👑 Admin stratejisi seçildi (3x ağırlık)
👑 Admin Değerlendirme: 5 yıldız × 3.0 = 15.0 puan
✏️ Editör stratejisi seçildi (2x ağırlık)
✏️ Editör Değerlendirme: 5 yıldız × 2.0 = 10.0 puan
👤 Normal kullanıcı stratejisi seçildi (1x ağırlık)
👤 Normal Kullanıcı Değerlendirme: 5 yıldız × 1.0 = 5.0 puan
```

### 2️⃣ Direkt Hesaplama

```java
@Autowired
private DegerlendirmeContext context;

public void hizliHesaplama() {
    // Tek satırda hesaplama
    double puan = context.hesapla("ADMIN", 4);
    // Sonuç: 12.0 (4 × 3)
    
    System.out.println("Puan: " + puan);
}
```

### 3️⃣ Birden Fazla Değerlendirme

```java
@Autowired
private DegerlendirmeContext context;

public void coklulDegerlendirme() {
    List<DegerlendirmeContext.Degerlendirme> degerlendirmeler = Arrays.asList(
        new DegerlendirmeContext.Degerlendirme("ADMIN", 5),    // 15 puan
        new DegerlendirmeContext.Degerlendirme("ADMIN", 4),    // 12 puan
        new DegerlendirmeContext.Degerlendirme("EDITOR", 5),   // 10 puan
        new DegerlendirmeContext.Degerlendirme("EDITOR", 3),   // 6 puan
        new DegerlendirmeContext.Degerlendirme("USER", 5),     // 5 puan
        new DegerlendirmeContext.Degerlendirme("USER", 4),     // 4 puan
        new DegerlendirmeContext.Degerlendirme("USER", 3)      // 3 puan
    );
    
    double toplamPuan = context.toplamPuanHesapla(degerlendirmeler);
    // Toplam: 55 puan
    
    double ortalama = context.ortalamaPuanHesapla(degerlendirmeler);
    // Ortalama: ~4.23/5.0
    
    System.out.println("Toplam Puan: " + toplamPuan);
    System.out.println("Ortalama: " + ortalama + "/5.0");
}
```

**Çıktı:**
```
📊 Toplam 7 değerlendirme hesaplanıyor...
👑 Admin Değerlendirme: 5 yıldız × 3.0 = 15.0 puan
👑 Admin Değerlendirme: 4 yıldız × 3.0 = 12.0 puan
✏️ Editör Değerlendirme: 5 yıldız × 2.0 = 10.0 puan
✏️ Editör Değerlendirme: 3 yıldız × 2.0 = 6.0 puan
👤 Normal Kullanıcı Değerlendirme: 5 yıldız × 1.0 = 5.0 puan
👤 Normal Kullanıcı Değerlendirme: 4 yıldız × 1.0 = 4.0 puan
👤 Normal Kullanıcı Değerlendirme: 3 yıldız × 1.0 = 3.0 puan
📈 Değerlendirme İstatistikleri:
   👑 Admin: 2 değerlendirme (3x ağırlık)
   ✏️ Editör: 2 değerlendirme (2x ağırlık)
   👤 Normal: 3 değerlendirme (1x ağırlık)
   💯 Toplam Puan: 55.0
⭐ Ortalama Puan: 4.23/5.0
```

### 4️⃣ Factory ile Kullanım

```java
@Autowired
private DegerlendirmeStratejiFactory factory;

public void factoryKullanimi() {
    // Factory'den strateji al
    DegerlendirmeStrateji strateji = factory.getStrateji("ADMIN");
    
    // Strateji ile hesaplama yap
    double puan = strateji.puanHesapla(5);
    
    System.out.println("Strateji: " + strateji.getStratejAdi());
    System.out.println("Ağırlık: " + strateji.getAgirlik());
    System.out.println("Puan: " + puan);
}
```

### 5️⃣ Service Entegrasyonu

```java
@Service
public class MacDegerlendirmeService {
    
    @Autowired
    private DegerlendirmeContext degerlendirmeContext;
    
    @Autowired
    private KullaniciRepository kullaniciRepository;
    
    public double macDegerlendir(Long macId, Long kullaniciId, int yildiz) {
        // Kullanıcıyı bul
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        // Kullanıcı rolüne göre puanı hesapla
        String rol = kullanici.getRol(); // "ADMIN", "EDITOR", "USER"
        double puan = degerlendirmeContext.hesapla(rol, yildiz);
        
        // Veritabanına kaydet
        // ...
        
        logger.info("Maç {} için {} değerlendirmesi: {} yıldız = {} puan", 
                   macId, rol, yildiz, puan);
        
        return puan;
    }
    
    public double macOrtalamasiHesapla(Long macId) {
        // Tüm değerlendirmeleri al
        List<MacDegerlendirme> degerlendirmeler = 
            degerlendirmeRepository.findByMacId(macId);
        
        // Değerlendirme listesine dönüştür
        List<DegerlendirmeContext.Degerlendirme> liste = 
            degerlendirmeler.stream()
                .map(d -> new DegerlendirmeContext.Degerlendirme(
                    d.getKullanici().getRol(), 
                    d.getYildizSayisi()
                ))
                .collect(Collectors.toList());
        
        // Ortalamayı hesapla (ağırlıklı)
        return degerlendirmeContext.ortalamaPuanHesapla(liste);
    }
}
```

## 🔑 Anahtar Kavramlar

### Strategy Interface
```java
public interface DegerlendirmeStrateji {
    double puanHesapla(int yildizSayisi);
    double getAgirlik();
    String getStratejAdi();
}
```

### Concrete Strategies
```java
// Admin: 3x ağırlık
public class AdminDegerlendirmeStrateji implements DegerlendirmeStrateji {
    public double puanHesapla(int yildiz) {
        return yildiz * 3.0;
    }
}

// Editör: 2x ağırlık
public class EditorDegerlendirmeStrateji implements DegerlendirmeStrateji {
    public double puanHesapla(int yildiz) {
        return yildiz * 2.0;
    }
}

// Normal: 1x ağırlık
public class NormalKullaniciDegerlendirmeStrateji implements DegerlendirmeStrateji {
    public double puanHesapla(int yildiz) {
        return yildiz * 1.0;
    }
}
```

### Context
```java
public class DegerlendirmeContext {
    private DegerlendirmeStrateji strateji;
    
    public void stratejiSec(String rol) {
        // Rol'e göre strateji seç
    }
    
    public double puanHesapla(int yildiz) {
        return strateji.puanHesapla(yildiz);
    }
}
```

## 📊 Avantajlar

| Avantaj | Açıklama |
|---------|----------|
| ✅ **Esneklik** | Runtime'da algoritma değiştirilebilir |
| ✅ **Open/Closed** | Yeni strateji eklemek kolay |
| ✅ **Kod Tekrarı Yok** | Her strateji ayrı sınıf |
| ✅ **Test Edilebilir** | Her strateji bağımsız test edilir |
| ✅ **Bakım Kolay** | Değişiklik sadece ilgili stratejide |

## 🎭 Diğer Pattern'lerle Karşılaştırma

| Pattern | Ne Zaman Kullan |
|---------|-----------------|
| **Strategy** | Algoritmanın tamamı değişecekse |
| **Template Method** | Algoritma iskeleti sabit, adımlar değişecekse |
| **State** | Nesne durumu davranışı etkiliyorsa |
| **Factory** | Nesne yaratma farklıysa |

## 🧪 Test Örneği

```java
@Test
public void testAdminStratejisi() {
    AdminDegerlendirmeStrateji strateji = new AdminDegerlendirmeStrateji();
    
    assertEquals(15.0, strateji.puanHesapla(5), 0.01);
    assertEquals(12.0, strateji.puanHesapla(4), 0.01);
    assertEquals(9.0, strateji.puanHesapla(3), 0.01);
    assertEquals(3.0, strateji.getAgirlik(), 0.01);
}

@Test
public void testDegerlendirmeContext() {
    DegerlendirmeContext context = new DegerlendirmeContext();
    
    // Admin
    double adminPuan = context.hesapla("ADMIN", 5);
    assertEquals(15.0, adminPuan, 0.01);
    
    // Editor
    double editorPuan = context.hesapla("EDITOR", 5);
    assertEquals(10.0, editorPuan, 0.01);
    
    // User
    double userPuan = context.hesapla("USER", 5);
    assertEquals(5.0, userPuan, 0.01);
}

@Test
public void testCokluDegerlendirme() {
    DegerlendirmeContext context = new DegerlendirmeContext();
    
    List<Degerlendirme> liste = Arrays.asList(
        new Degerlendirme("ADMIN", 5),  // 15
        new Degerlendirme("EDITOR", 4), // 8
        new Degerlendirme("USER", 3)    // 3
    );
    
    double toplam = context.toplamPuanHesapla(liste);
    assertEquals(26.0, toplam, 0.01); // 15 + 8 + 3
}
```

## 🚀 Gerçek Hayat Senaryoları

### Senaryo 1: Maç Değerlendirme
```
Galatasaray - Fenerbahçe maçı:
- Admin A: 5 yıldız → 15 puan
- Admin B: 4 yıldız → 12 puan
- Editör A: 5 yıldız → 10 puan
- Editör B: 4 yıldız → 8 puan
- User A: 5 yıldız → 5 puan
- User B: 4 yıldız → 4 puan
- User C: 3 yıldız → 3 puan

Toplam: 57 puan
Ortalama: 4.38/5.0 ⭐⭐⭐⭐
```

### Senaryo 2: Dinamik Ağırlık Değişimi
```java
// Farklı durumlarda farklı ağırlıklar
public class OzelGunStrateji implements DegerlendirmeStrateji {
    public double puanHesapla(int yildiz) {
        // Özel günlerde (derbi, final vs.) 
        // admin ağırlığı 5x olabilir
        return yildiz * 5.0;
    }
}
```

## 📝 Notlar

- ✅ Her strateji **@Component** olmalı (Spring DI için)
- ✅ Context **strateji değişimini** yönetir
- ✅ Factory **strateji seçimini** kolaylaştırır
- ✅ **Runtime'da** strateji değiştirilebilir
- ✅ Yeni strateji eklemek **çok kolay**

## 🎯 Pattern Kombinasyonları

Bu projede **Strategy + Factory** kombinasyonu kullanıldı:
```
DegerlendirmeStratejiFactory (Factory Pattern)
└── Stratejileri oluşturur/döndürür

DegerlendirmeContext (Strategy Pattern)
└── Stratejileri kullanır
```

## 🎓 SOLID Prensipleri

- ✅ **S**ingle Responsibility: Her strateji tek sorumluluk
- ✅ **O**pen/Closed: Yeni strateji eklenebilir, mevcut kod değişmez
- ✅ **L**iskov Substitution: Stratejiler birbirinin yerine kullanılabilir
- ✅ **I**nterface Segregation: Minimal interface
- ✅ **D**ependency Inversion: Interface'e bağımlılık

## 🎯 Sonuç

Strategy Pattern sayesinde:
- ✅ Admin, Editör, Normal kullanıcı değerlendirmeleri farklı ağırlıkta
- ✅ Yeni kullanıcı tipi eklemek çok kolay
- ✅ Ağırlıkları değiştirmek çok kolay
- ✅ Her strateji bağımsız test edilebilir
- ✅ Runtime'da strateji değiştirilebilir

**🎯 Strategy Pattern - Algoritmaları Değiştirilebilir Yap!**

