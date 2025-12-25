package com.footbase.controller;

import com.footbase.entity.Mac;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.MacDurumGecmisiRepository;
import com.footbase.repository.MacMedyaRepository;
import com.footbase.repository.MacOyuncuOlaylariRepository;
import com.footbase.repository.MacTakimlariRepository;
import com.footbase.security.JwtUtil;
import com.footbase.service.MacService;
import com.footbase.service.YorumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Maç controller'ı
 * Maç işlemleri endpoint'lerini içerir
 */
@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "http://localhost:3000")
public class MacController {

    @Autowired
    private MacService macService;

    @Autowired
    private YorumService yorumService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private KullaniciRepository kullaniciRepository;
    
    @Autowired
    private MacTakimlariRepository macTakimlariRepository;
    
    @Autowired
    private MacOyuncuOlaylariRepository macOyuncuOlaylariRepository;
    
    @Autowired
    private MacMedyaRepository macMedyaRepository;
    
    @Autowired
    private MacDurumGecmisiRepository macDurumGecmisiRepository;

    /**
     * Tüm maçları getirir
     * @return Maç listesi
     */
    @GetMapping
    public ResponseEntity<List<Mac>> tumMaclariGetir() {
        return ResponseEntity.ok(macService.tumMaclariGetir());
    }

    /**
     * ID'ye göre maç getirir
     * @param id Maç ID'si
     * @return Maç bilgileri
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> macGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macService.macGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    /**
     * Maça ait yorumları getirir
     * @param id Maç ID'si
     * @return Yorum listesi
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> macYorumlariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(yorumService.macYorumlariniGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    /**
     * Maça yorum ekler
     * @param id Maç ID'si
     * @param yorumBilgileri Yorum mesajı içeren map
     * @param request HTTP request (JWT token içerir)
     * @return Oluşturulan yorum
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> yorumEkle(@PathVariable Long id, @RequestBody Map<String, String> yorumBilgileri, HttpServletRequest request) {
        try {
            String mesaj = yorumBilgileri.get("message");
            if (mesaj == null || mesaj.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Yorum mesajı gereklidir"));
            }
            
            // Kullanıcı ID'sini JWT token'dan al
            Long kullaniciId = null;
            try {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    String email = jwtUtil.getKullaniciEmailFromToken(token);
                    kullaniciId = kullaniciRepository.findByEmail(email)
                            .map(k -> k.getId())
                            .orElse(null);
                }
            } catch (Exception e) {
                // Token yoksa veya geçersizse devam et
            }
            
            // Kullanıcı ID'si yoksa hata döndür
            if (kullaniciId == null) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            
            return ResponseEntity.ok(yorumService.yorumOlustur(id, kullaniciId, mesaj.trim()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    /**
     * Maç takımlarını getirir
     * @param id Maç ID'si
     * @return Takım listesi
     */
    @GetMapping("/{id}/teams")
    public ResponseEntity<?> macTakimlariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macTakimlariRepository.findByMacIdWithDetails(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    /**
     * Maç olaylarını getirir (gol, kart)
     * @param id Maç ID'si
     * @return Olay listesi
     */
    @GetMapping("/{id}/events")
    public ResponseEntity<?> macOlaylariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macOyuncuOlaylariRepository.findByMacIdWithDetails(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    /**
     * Maç medyasını getirir
     * @param id Maç ID'si
     * @return Medya listesi
     */
    @GetMapping("/{id}/media")
    public ResponseEntity<?> macMedyasiniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macMedyaRepository.findByMacIdWithDetails(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    /**
     * Maç durum geçmişini getirir
     * @param id Maç ID'si
     * @return Durum geçmişi listesi
     */
    @GetMapping("/{id}/status-history")
    public ResponseEntity<?> macDurumGecmisiniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macDurumGecmisiRepository.findByMacIdWithDetails(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

}

