# FACTORY METHOD PATTERN - Kullanıcı Yönetimi

## 📚 Genel Bakış

Factory Method pattern'i kullanarak farklı kullanıcı tiplerini (Admin, Editor, User) oluşturuyoruz. Bu pattern, nesne oluşturma mantığını merkezi hale getirerek kod tekrarını önler ve sistemi daha esnek hale getirir.

## 🎯 Problem

Sistemde 3 farklı kullanıcı tipi var:
- **Admin**: Tüm yetkilere sahip yönetici
- **Editor**: Maç ekleme ve yönetme yetkisi olan editör
- **User**: Sadece görüntüleme ve yorum yapma yetkisi olan normal kullanıcı

Her kullanıcı tipi için ayrı ayrı nesne oluşturma kodu yazmak:
- Kod tekrarına neden olur
- Hata yapma ihtimalini artırır
- Yeni kullanıcı tipi eklemek zorlaşır
- Test edilmesi zor kodlar oluşturur

## ✅ Çözüm: Factory Method Pattern

```
┌─────────────────────────────────────────┐
│        KullaniciFactory                 │
│  (Merkezi nesne üretim noktası)        │
└────────────┬────────────────────────────┘
             │
             │ createKullanici(rol)
             │
   ┌─────────┴──────────┬──────────────┐
   │                    │              │
   ▼                    ▼              ▼
┌────────┐      ┌──────────┐    ┌──────────┐
│ Admin  │      │  Editor  │    │   User   │
│Kullanici│      │Kullanici │    │Kullanici │
└────────┘      └──────────┘    └──────────┘
```

## 📁 Dosya Yapısı

```
backend/src/main/java/com/footbase/patterns/factory/
├── Kullanici.java              # Interface (Product)
├── AdminKullanici.java         # Concrete Product
├── EditorKullanici.java        # Concrete Product
├── NormalKullanici.java        # Concrete Product
├── KullaniciFactory.java       # Creator (Factory)
└── README_FACTORY_PATTERN.md   # Bu dosya
```

## 💻 Kullanım Örnekleri

### 1. Basit Kullanım

```java
// Admin oluştur
Kullanici admin = KullaniciFactory.createKullanici("ADMIN");
admin.login();  // 👨‍💼 Admin giriş yaptı

// Editor oluştur
Kullanici editor = KullaniciFactory.createKullanici("EDITOR");
editor.login();  // ✍️ Editör giriş yaptı

// Normal kullanıcı oluştur
Kullanici user = KullaniciFactory.createKullanici("USER");
user.login();  // 👤 Kullanıcı giriş yaptı
```

### 2. İsimle Kullanım

```java
Kullanici admin = KullaniciFactory.createKullanici("ADMIN", "Ahmet Admin");
Kullanici editor = KullaniciFactory.createKullanici("EDITOR", "Mehmet Editor");
Kullanici user = KullaniciFactory.createKullanici("USER", "Ali Kullanıcı");
```

### 3. Entity'den Oluşturma

```java
// Veritabanından gelen entity
com.footbase.entity.Kullanici kullaniciEntity = kullaniciRepository.findById(1L).orElseThrow();

// Factory pattern kullanıcı oluştur
Kullanici kullanici = KullaniciFactory.fromEntity(kullaniciEntity);
kullanici.login();
```

### 4. Yetki Kontrolü

```java
Kullanici admin = KullaniciFactory.createKullanici("ADMIN");

// Belirli bir yetkiyi kontrol et
if (admin.hasPermission("MATCH_APPROVE")) {
    System.out.println("✅ Maç onaylama yetkisi var!");
    // Maç onaylama işlemi...
}

// Tüm yetkileri göster
List<String> permissions = admin.getPermissions();
permissions.forEach(p -> System.out.println("  - " + p));
```

### 5. Authentication Service'te Kullanım

```java
@Service
public class AuthenticationService {
    
    public Kullanici authenticate(String email, String password) {
        // Veritabanından kullanıcıyı bul
        com.footbase.entity.Kullanici entity = kullaniciRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        // Şifre kontrolü
        if (!passwordEncoder.matches(password, entity.getSifre())) {
            throw new RuntimeException("Şifre hatalı");
        }
        
        // Factory ile kullanıcı oluştur
        Kullanici kullanici = KullaniciFactory.fromEntity(entity);
        kullanici.login();
        
        return kullanici;
    }
}
```

## 🔐 Yetki Tablosu

| Yetki | Admin | Editor | User |
|-------|-------|--------|------|
| MATCH_CREATE | ✅ | ✅ | ❌ |
| MATCH_APPROVE | ✅ | ❌ | ❌ |
| MATCH_EDIT_OWN | ✅ | ✅ | ❌ |
| MATCH_UPDATE_SCORE | ✅ | ✅ | ❌ |
| USER_MANAGE | ✅ | ❌ | ❌ |
| COMMENT_ADD | ✅ | ✅ | ✅ |
| COMMENT_DELETE | ✅ | ❌ | Sadece kendi |
| PLAYER_RATE | ✅ | ❌ | ✅ |
| SYSTEM_SETTINGS | ✅ | ❌ | ❌ |

## 🎨 UML Diyagram

```
┌─────────────────────────┐
│     <<interface>>       │
│      Kullanici          │
├─────────────────────────┤
│ + login()               │
│ + getPermissions()      │
│ + getRole()             │
│ + hasPermission()       │
└──────────▲──────────────┘
           │
           │ implements
     ┌─────┴─────┬─────────┐
     │           │         │
┌────┴────┐ ┌───┴─────┐ ┌─┴────────┐
│  Admin  │ │ Editor  │ │  Normal  │
│Kullanici│ │Kullanici│ │Kullanici │
└─────────┘ └─────────┘ └──────────┘
     ▲           ▲           ▲
     │           │           │
     └───────────┴───────────┘
              creates
        ┌──────────────────┐
        │KullaniciFactory  │
        │+ createKullanici()│
        └──────────────────┘
```

## ✨ Avantajlar

### 1. **Kod Tekrarını Önler**
```java
// ❌ Önce (Tekrarlı kod)
if (rol.equals("ADMIN")) {
    AdminKullanici admin = new AdminKullanici();
    admin.login();
    // ...
} else if (rol.equals("EDITOR")) {
    EditorKullanici editor = new EditorKullanici();
    editor.login();
    // ...
}

// ✅ Sonra (Factory ile)
Kullanici kullanici = KullaniciFactory.createKullanici(rol);
kullanici.login();
```

### 2. **Yeni Tip Eklemek Kolay**
Yeni bir `ModeratörKullanici` eklemek için:
1. `Kullanici` interface'ini implement et
2. `KullaniciFactory`'ye yeni case ekle
3. Bitirdin! ✅

### 3. **Test Edilebilir**
```java
@Test
public void testAdminCreation() {
    Kullanici admin = KullaniciFactory.createKullanici("ADMIN");
    assertEquals("ADMIN", admin.getRole());
    assertTrue(admin.hasPermission("MATCH_APPROVE"));
}
```

### 4. **Merkezi Kontrol**
Tüm kullanıcı oluşturma mantığı tek yerde → Değişiklik yapmak kolay

## 🔄 Observer Pattern ile Entegrasyon

Factory pattern ile Observer pattern birlikte çalışır:

```java
// Factory ile kullanıcı oluştur
Kullanici admin = KullaniciFactory.createKullanici("ADMIN", "Ahmet Admin");

// Observer pattern için gözlemci oluştur
YoneticiGozlemci yoneticiGozlemci = new YoneticiGozlemci(
    kullaniciRepository.findByEmail("ahmet@admin.com").orElseThrow()
);

// Konuya ekle
macOnayKonusu.ekle(yoneticiGozlemci);
```

## 📊 Gerçek Dünya Kullanımı

### Login Controller
```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        // Kullanıcıyı doğrula
        com.footbase.entity.Kullanici entity = authenticate(request);
        
        // Factory ile kullanıcı oluştur
        Kullanici kullanici = KullaniciFactory.fromEntity(entity);
        kullanici.login();
        
        // Yetkileri döndür
        return ResponseEntity.ok(Map.of(
            "token", generateToken(entity),
            "role", kullanici.getRole(),
            "permissions", kullanici.getPermissions()
        ));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Giriş başarısız");
    }
}
```

## 🎓 Design Principles

Bu pattern şu SOLID prensiplerini uygular:

1. **Single Responsibility**: Her sınıf tek bir sorumluluğa sahip
2. **Open/Closed**: Yeni kullanıcı tipi eklemek için mevcut kodu değiştirmiyoruz
3. **Liskov Substitution**: Her kullanıcı tipi `Kullanici` yerine kullanılabilir
4. **Interface Segregation**: `Kullanici` interface'i minimal ve odaklı
5. **Dependency Inversion**: Yüksek seviye kod soyut interface'e bağımlı

## 🚀 Sonuç

Factory Method pattern sayesinde:
- ✅ Daha temiz kod
- ✅ Daha az hata
- ✅ Daha kolay bakım
- ✅ Daha iyi test edilebilirlik
- ✅ Daha esnek mimari

**FootBase projesinde kullanıcı yönetimi artık profesyonel bir tasarım pattern'i ile yönetiliyor!** 🎉

