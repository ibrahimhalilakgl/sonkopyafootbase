# Veritabanı Bağlantı Sorunu Çözümü

## Sorun
Backend başlatıldığında şu hata alınıyor:
```
FATAL: password authentication failed for user "postgres"
```

## Çözüm

1. **PostgreSQL şifrenizi kontrol edin:**
   - PostgreSQL'in kurulumunda belirlediğiniz şifreyi kullanın
   - Eğer şifrenizi unuttuysanız, PostgreSQL'i yeniden kurun veya şifreyi sıfırlayın

2. **application.properties dosyasını düzenleyin:**
   - `backend/src/main/resources/application.properties` dosyasını açın
   - `spring.datasource.password=YOUR_POSTGRES_PASSWORD_HERE` satırını bulun
   - `YOUR_POSTGRES_PASSWORD_HERE` yerine gerçek PostgreSQL şifrenizi yazın

3. **Veritabanı adını kontrol edin:**
   - PostgreSQL'de veritabanı adı `footbase` mi yoksa `FootBase` mi?
   - Eğer `FootBase` ise, `spring.datasource.url` satırını şu şekilde değiştirin:
     ```
     spring.datasource.url=jdbc:postgresql://localhost:5432/FootBase
     ```

4. **PostgreSQL'in çalıştığından emin olun:**
   - PostgreSQL servisinin çalıştığını kontrol edin
   - Windows'ta: Services (Hizmetler) uygulamasından "postgresql" servisini kontrol edin

## Örnek Doğru Yapılandırma

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/footbase
spring.datasource.username=postgres
spring.datasource.password=gerçek_şifreniz_buraya
spring.datasource.driver-class-name=org.postgresql.Driver
```

## Test

Şifreyi düzelttikten sonra backend'i tekrar başlatın:
```powershell
cd C:\Users\Teatl\OneDrive\Desktop\footbasesonn\backend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
.\mvnw.cmd spring-boot:run
```

Başarılı başlatma sonrası şu mesajı görmelisiniz:
```
Started FootBaseApplication in X.XXX seconds
```



