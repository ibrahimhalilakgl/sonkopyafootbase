package com.footbase.dto;

import com.footbase.entity.Mac;
import com.footbase.entity.MacDurumGecmisi;
import com.footbase.entity.MacMedya;
import com.footbase.entity.MacOyuncuOlaylari;
import com.footbase.entity.MacTakimlari;

import java.util.List;

/**
 * Maç Detay DTO
 * 
 * Facade Pattern ile kullanılır.
 * Bir maçın tüm detaylarını tek bir nesnede toplar:
 * - Maç bilgileri
 * - Takımlar
 * - Olaylar (goller, kartlar)
 * - Medya (fotoğraflar, videolar)
 * - Durum geçmişi
 * 
 * Bu sayede frontend tek HTTP isteği ile tüm bilgileri alabilir.
 */
public class MacDetayDTO {
    
    private Mac mac;
    private List<MacTakimlari> takimlar;
    private List<MacOyuncuOlaylari> olaylar;
    private List<MacMedya> medya;
    private List<MacDurumGecmisi> durumGecmisi;
    
    public MacDetayDTO() {
    }
    
    public MacDetayDTO(Mac mac, List<MacTakimlari> takimlar, List<MacOyuncuOlaylari> olaylar,
                       List<MacMedya> medya, List<MacDurumGecmisi> durumGecmisi) {
        this.mac = mac;
        this.takimlar = takimlar;
        this.olaylar = olaylar;
        this.medya = medya;
        this.durumGecmisi = durumGecmisi;
    }
    
    // Getters and Setters
    
    public Mac getMac() {
        return mac;
    }
    
    public void setMac(Mac mac) {
        this.mac = mac;
    }
    
    public List<MacTakimlari> getTakimlar() {
        return takimlar;
    }
    
    public void setTakimlar(List<MacTakimlari> takimlar) {
        this.takimlar = takimlar;
    }
    
    public List<MacOyuncuOlaylari> getOlaylar() {
        return olaylar;
    }
    
    public void setOlaylar(List<MacOyuncuOlaylari> olaylar) {
        this.olaylar = olaylar;
    }
    
    public List<MacMedya> getMedya() {
        return medya;
    }
    
    public void setMedya(List<MacMedya> medya) {
        this.medya = medya;
    }
    
    public List<MacDurumGecmisi> getDurumGecmisi() {
        return durumGecmisi;
    }
    
    public void setDurumGecmisi(List<MacDurumGecmisi> durumGecmisi) {
        this.durumGecmisi = durumGecmisi;
    }
}

