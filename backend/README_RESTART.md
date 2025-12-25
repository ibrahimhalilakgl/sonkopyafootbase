# Backend Yeniden Başlatma Talimatları

## Sorun
Frontend'de 404 hataları görünüyor çünkü backend yeni endpoint'leri yüklemedi.

## Çözüm: Backend'i Yeniden Başlatın

### Yöntem 1: Maven Wrapper ile
```bash
cd backend
.\mvnw.cmd spring-boot:run
```

### Yöntem 2: IDE'den
1. IDE'nizde (IntelliJ IDEA, Eclipse, VS Code) Spring Boot uygulamasını durdurun
2. `FootBaseApplication.java` dosyasını açın
3. "Run" veya "Debug" butonuna tıklayın

### Yöntem 3: Terminal'den (Maven yüklüyse)
```bash
cd backend
mvn spring-boot:run
```

## Kontrol
Backend başladıktan sonra şu endpoint'leri test edin:
- http://localhost:8080/api/players/1/score
- http://localhost:8080/api/players/1/media
- http://localhost:8080/api/players/1/statistics
- http://localhost:8080/api/players/1/comments

## Yeni Endpoint'ler

### Oyuncu Endpoint'leri
- `GET /api/players/{id}/score` - Oyuncu puanı
- `GET /api/players/{id}/media` - Oyuncu medyası
- `GET /api/players/{id}/statistics` - Oyuncu istatistikleri
- `GET /api/players/{id}/comments` - Oyuncu yorumları

### Maç Endpoint'leri
- `GET /api/matches/{id}/teams` - Maç takımları
- `GET /api/matches/{id}/events` - Maç olayları
- `GET /api/matches/{id}/media` - Maç medyası
- `GET /api/matches/{id}/status-history` - Durum geçmişi

### Takım Endpoint'leri
- `GET /api/teams/{id}/players` - Takım oyuncuları
- `GET /api/teams/{id}/matches` - Takım maçları
- `GET /api/teams/{id}/statistics` - Takım istatistikleri

