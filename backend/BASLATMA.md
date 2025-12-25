# Backend Başlatma Rehberi

## Hızlı Başlatma

Backend'i başlatmak için aşağıdaki komutu çalıştırın:

```powershell
cd C:\Users\Teatl\OneDrive\Desktop\footbasesonn\backend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
.\mvnw.cmd spring-boot:run
```

## Adım Adım

1. **PowerShell'i açın** ve backend klasörüne gidin:
```powershell
cd C:\Users\Teatl\OneDrive\Desktop\footbasesonn\backend
```

2. **JAVA_HOME'u ayarlayın** (eğer kalıcı olarak ayarlanmadıysa):
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

3. **Backend'i başlatın**:
```powershell
.\mvnw.cmd spring-boot:run
```

## Veritabanı Ayarları

Backend PostgreSQL veritabanına bağlanacak şekilde yapılandırılmıştır:

- **Veritabanı:** FootBase
- **Host:** localhost:5432
- **Kullanıcı:** postgres
- **Şifre:** 1234

Bu ayarları değiştirmek için `src/main/resources/application.properties` dosyasını düzenleyin.

## Uygulama Adresi

Backend başarıyla başlatıldıktan sonra şu adreste çalışacaktır:
- **URL:** http://localhost:8080
- **API Base:** http://localhost:8080/api

## Sorun Giderme

### JAVA_HOME Hatası
Eğer JAVA_HOME hatası alırsanız:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

### Port Zaten Kullanılıyor
8080 portu kullanılıyorsa, `application.properties` dosyasında `server.port` değerini değiştirin.

### Veritabanı Bağlantı Hatası
PostgreSQL'in çalıştığından ve veritabanı bilgilerinin doğru olduğundan emin olun.



