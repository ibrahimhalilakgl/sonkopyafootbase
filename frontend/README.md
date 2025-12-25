# FootBase Frontend

FootBase - Futbol Maç Yorumlama ve Oyuncu Puanlama Platformu Frontend Uygulaması

## Kurulum

```bash
npm install
```

## Çalıştırma

```bash
npm start
```

Uygulama http://localhost:3000 adresinde çalışacaktır.

## Backend API

Backend API varsayılan olarak http://localhost:8080/api adresinde çalışır.

API URL'ini değiştirmek için `.env` dosyası oluşturun:
```
API_URL=http://localhost:8080/api
```

## Özellikler

- ✅ Maç listesi ve detayları
- ✅ Oyuncu listesi ve detayları
- ✅ Maç yorumları
- ✅ Oyuncu puanlama
- ✅ Kullanıcı profil ve takip sistemi
- ✅ Feed (Takip edilenlerin aktiviteleri)
- ✅ Admin paneli (maç/takım/oyuncu yönetimi)

## Yapı

- `app/utils/api.js` - Backend API çağrıları
- `app/utils/constants.js` - API endpoint'leri ve sabitler
- `app/utils/request.js` - HTTP istek yardımcı fonksiyonları
- `app/containers/Pages/` - Ana sayfa bileşenleri

