# JAVA_HOME Ayarlama Rehberi

## Yöntem 1: Geçici Ayarlama (Sadece Mevcut PowerShell Oturumu İçin)

PowerShell'de şu komutu çalıştırın:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

Ardından kontrol edin:
```powershell
echo $env:JAVA_HOME
```

## Yöntem 2: Kalıcı Ayarlama (Sistem Genelinde)

### Adım 1: Sistem Ortam Değişkenlerini Açın

1. Windows tuşuna basın ve "ortam değişkenleri" yazın
2. "Ortam değişkenlerini düzenle" seçeneğine tıklayın

### Adım 2: JAVA_HOME Değişkenini Ekleyin/Düzenleyin

1. "Sistem değişkenleri" bölümünde "Yeni..." butonuna tıklayın
2. Değişken adı: `JAVA_HOME`
3. Değişken değeri: `C:\Program Files\Java\jdk-21`
4. "Tamam" butonuna tıklayın

### Adım 3: PATH Değişkenini Güncelleyin

1. "Sistem değişkenleri" bölümünde `Path` değişkenini bulun
2. "Düzenle..." butonuna tıklayın
3. "Yeni" butonuna tıklayın
4. Şu değeri ekleyin: `%JAVA_HOME%\bin`
5. "Tamam" butonuna tıklayın

### Adım 4: Yeni PowerShell Penceresi Açın

Değişikliklerin etkili olması için yeni bir PowerShell penceresi açın ve kontrol edin:

```powershell
echo $env:JAVA_HOME
java -version
```

## Hızlı PowerShell Komutu (Kalıcı Ayarlama İçin)

Aşağıdaki komutu **Yönetici olarak çalıştırılan PowerShell**'de çalıştırın:

```powershell
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", [System.EnvironmentVariableTarget]::Machine)
```

Ardından yeni bir PowerShell penceresi açın.

## Kontrol

JAVA_HOME'un doğru ayarlandığını kontrol etmek için:

```powershell
echo $env:JAVA_HOME
```

Çıktı şöyle olmalı: `C:\Program Files\Java\jdk-21`



