# 🎨 Template Method Pattern

## 📌 Ne İşe Yarar?

Template Method Pattern, bir algoritmanın iskeletini tanımlar. Bazı adımları alt sınıflar özelleştirir, ama algoritmanın yapısı değişmez.

## 🎯 Projede Kullanımı

Maç işleme workflow'u için kullanılıyor:

```
MacIslemSablonu (Abstract)
├── MacOlusturmaTemplate     → Yeni maç oluşturma
├── MacOnaylamaTemplate       → Maç onaylama/reddetme  
└── MacGuncellemeTemplate     → Maç güncelleme
```

## 📋 İşlem Akışı (Template)

```
1. ✓ Ön Kontroller
2. ✓ Veri Doğrulama
3. ⚙️ Maç İşleme (alt sınıf özelleştirir)
4. 💾 Kaydetme
5. 📧 Bildirim Gönderme (opsiyonel)
6. 🏁 Son İşlemler
```

## 💻 Kullanım Örnekleri

### 1. Yeni Maç Oluşturma

```java
@Autowired
private MacOlusturmaTemplate macOlusturma;

public void yeniMacEkle(Mac mac) {
    boolean basarili = macOlusturma.macIsle(mac);
    
    if (basarili) {
        System.out.println("✅ Maç başarıyla oluşturuldu!");
    }
}
```

**Çıktı:**
```
🎯 Maç işleme başlatılıyor... [MacOlusturmaTemplate]
🔍 Ön kontroller yapılıyor...
✓ Ön kontroller başarılı
✓ Veri doğrulama yapılıyor...
⚙️ Maç işleniyor...
🆕 Yeni maç oluşturuluyor...
✅ Maç oluşturuldu - Onay bekliyor
💾 Yeni maç veritabanına kaydediliyor...
✅ Maç başarıyla kaydedildi
📧 Bildirimler gönderiliyor...
📧 Admin'e yeni maç bildirimi gönderiliyor...
🏁 Maç oluşturma işlemi tamamlandı
✅ Maç işleme tamamlandı!
```

### 2. Maç Onaylama

```java
@Autowired
private MacOnaylamaTemplate macOnaylama;

public void macOnayla(Mac mac) {
    macOnaylama.setOnayla(true);  // true: onayla, false: reddet
    boolean basarili = macOnaylama.macIsle(mac);
    
    if (basarili) {
        System.out.println("✅ Maç yayına alındı!");
    }
}
```

**Çıktı:**
```
🎯 Maç işleme başlatılıyor... [MacOnaylamaTemplate]
🔍 Ön kontroller yapılıyor...
🔍 Onaylama için ek kontroller...
✓ Onaylama kontrolleri başarılı
⚙️ Maç işleniyor...
✅ Maç onaylanıyor...
✅ Maç YAYINDA durumuna getirildi
💾 Onay durumu kaydediliyor...
📧 Bildirimler gönderiliyor...
📧 Editöre maç onaylandı bildirimi gönderiliyor...
✅ Maç yayına alındı
📝 Durum geçmişine kaydediliyor...
🎉 Maç başarıyla yayına alındı!
✅ Maç işleme tamamlandı!
```

### 3. Maç Güncelleme

```java
@Autowired
private MacGuncellemeTemplate macGuncelleme;

public void macGuncelle(Mac mac) {
    boolean basarili = macGuncelleme.macIsle(mac);
    
    if (basarili) {
        System.out.println("✅ Maç güncellendi!");
    }
}
```

**Çıktı:**
```
🎯 Maç işleme başlatılıyor... [MacGuncellemeTemplate]
🔍 Ön kontroller yapılıyor...
🔍 Güncelleme için ek kontroller...
✓ Güncelleme kontrolleri başarılı
🔍 Güncelleme için veri doğrulama...
⚙️ Maç işleniyor...
✏️ Maç güncelleniyor...
📅 Tarih değişti: 2024-12-25 → 2024-12-26
✅ Maç bilgileri güncellendi
💾 Maç güncellemeleri kaydediliyor...
✅ Maç başarıyla güncellendi
🏁 Maç güncelleme işlemi tamamlandı
✅ Maç işleme tamamlandı!
```

## 🔑 Anahtar Kavramlar

### Template Method (final)
```java
public final boolean macIsle(Mac mac) {
    // İskeleti değiştirilemez!
    onKontrollerYap();
    verileriDogrula();
    maciIsle();        // Alt sınıf özelleştirir
    kaydet();
    bildirimGonder();
    sonIslemler();
}
```

### Abstract Method
```java
// Alt sınıflar MUTLAKA implement etmeli
protected abstract void maciIsle(Mac mac);
```

### Hook Method
```java
// Opsiyonel - Alt sınıflar isterse override eder
protected boolean bildirimGonder() {
    return false; // Varsayılan değer
}
```

### Concrete Method
```java
// Varsayılan implementasyon var, override edilebilir
protected boolean onKontrollerYap(Mac mac) {
    // Varsayılan kontroller
    return true;
}
```

## 📊 Avantajlar

| Avantaj | Açıklama |
|---------|----------|
| ✅ **Kod Tekrarını Önler** | Ortak işlemler template'de |
| ✅ **Tutarlılık** | Her işlem aynı akışı takip eder |
| ✅ **Esneklik** | Alt sınıflar adımları özelleştirir |
| ✅ **Bakım Kolaylığı** | Algoritma değişikliği tek yerde |
| ✅ **Anlaşılabilirlik** | İş akışı net görülür |

## 🎭 Diğer Pattern'lerle Karşılaştırma

| Pattern | Ne Zaman |
|---------|----------|
| **Template Method** | İşlem adımları sabit, implementasyon değişken |
| **Strategy** | Tüm algoritma değiştirilebilir |
| **Factory** | Nesne yaratma farklı |
| **Observer** | Olay bildirimleri için |

## 🧪 Test Örneği

```java
@Test
public void testMacOlusturma() {
    Mac mac = new Mac();
    mac.setTarih(LocalDate.now().plusDays(7));
    mac.setSaat(LocalTime.of(20, 0));
    
    MacOlusturmaTemplate template = new MacOlusturmaTemplate();
    boolean sonuc = template.macIsle(mac);
    
    assertTrue(sonuc);
    assertEquals("ONAY_BEKLIYOR", mac.getOnayDurumu());
}
```

## 🚀 Gerçek Kullanım (Service'te)

```java
@Service
public class MacIslemService {
    
    @Autowired
    private MacOlusturmaTemplate macOlusturma;
    
    @Autowired
    private MacOnaylamaTemplate macOnaylama;
    
    @Autowired
    private MacGuncellemeTemplate macGuncelleme;
    
    public Mac yeniMacOlustur(Mac mac) {
        boolean basarili = macOlusturma.macIsle(mac);
        if (!basarili) {
            throw new RuntimeException("Maç oluşturulamadı!");
        }
        return mac;
    }
    
    public Mac macOnayla(Long macId, boolean onayla) {
        Mac mac = macRepository.findById(macId)
            .orElseThrow(() -> new RuntimeException("Maç bulunamadı"));
        
        macOnaylama.setOnayla(onayla);
        boolean basarili = macOnaylama.macIsle(mac);
        
        if (!basarili) {
            throw new RuntimeException("Maç onaylanamadı!");
        }
        return mac;
    }
    
    public Mac macGuncelle(Mac mac) {
        boolean basarili = macGuncelleme.macIsle(mac);
        if (!basarili) {
            throw new RuntimeException("Maç güncellenemedi!");
        }
        return mac;
    }
}
```

## 📝 Notlar

- Template method **final** olmalı (değiştirilemez)
- Abstract metodlar alt sınıflar tarafından **mutlaka** implement edilmeli
- Hook metodlar **opsiyonel** - varsayılan davranış var
- Concrete metodlar **override edilebilir** - varsayılan implementasyon var

## 🎯 Sonuç

Template Method Pattern sayesinde:
- ✅ Maç işleme akışı standardize edildi
- ✅ Kod tekrarı önlendi
- ✅ Yeni işlem tipleri kolayca eklenebilir
- ✅ Tutarlı ve güvenilir işlemler

**🎨 Template Method Pattern - Algoritma İskeletini Tanımla!**

