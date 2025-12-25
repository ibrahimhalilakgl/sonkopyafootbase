# Backend Yeniden Başlatma - ACİL

## Sorun
Endpoint'ler 404 hatası veriyor: "No static resource api/players/16/score"
Bu, Spring Boot'un controller'ları bulamadığı anlamına geliyor.

## ÇÖZÜM: Backend'i TAMAMEN DURDURUP YENİDEN BAŞLATIN

### Adım 1: Backend'i DURDURUN
1. IDE'de (IntelliJ IDEA / Eclipse / VS Code) çalışan backend'i DURDURUN
2. VEYA terminal'de Ctrl+C ile durdurun
3. **Tüm Java process'lerini kapatın** (Task Manager'dan kontrol edin)

### Adım 2: Temiz Build Yapın
Terminal'de şu komutu çalıştırın:
```powershell
cd backend
.\mvnw.cmd clean compile
```

### Adım 3: Backend'i YENİDEN BAŞLATIN

#### Yöntem 1: IDE'den (Önerilen)
1. `FootBaseApplication.java` dosyasını açın
2. **Run** veya **Debug** butonuna tıklayın
3. Backend'in tamamen başladığından emin olun (log'larda "Started FootBaseApplication" mesajını görmelisiniz)

#### Yöntem 2: Terminal'den
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### Adım 4: Kontrol Edin

Backend başladıktan sonra şu endpoint'leri test edin:

1. **Root endpoint (çalışmalı):**
   - http://localhost:8080/

2. **Player endpoint'leri (çalışmalı):**
   - http://localhost:8080/api/players
   - http://localhost:8080/api/players/16
   - http://localhost:8080/api/players/16/score
   - http://localhost:8080/api/players/16/media
   - http://localhost:8080/api/players/16/statistics
   - http://localhost:8080/api/players/16/comments

3. **Swagger UI (çalışmalı):**
   - http://localhost:8080/swagger-ui.html

## Önemli Notlar

- Backend'i durdurmadan önce TÜM çalışan instance'ları kapatın
- Clean build yapmak önemli - eski class dosyaları kalmamalı
- Backend tamamen başlayana kadar bekleyin (log'larda hata olmamalı)
- Veritabanı bağlantısı çalışıyorsa log'larda SQL sorgularını görebilirsiniz

## Sorun Devam Ederse

Eğer hala 404 hatası alıyorsanız:

1. Backend log'larını kontrol edin - controller'lar yükleniyor mu?
2. `target/classes/com/footbase/controller/` klasöründe class dosyalarının olduğundan emin olun
3. Backend log'larında "Mapped" kelimesini arayın - endpoint mapping'lerini gösterir

