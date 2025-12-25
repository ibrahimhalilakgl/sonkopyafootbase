package com.footbase.controller;

import com.footbase.repository.KullaniciRepository;
import com.footbase.security.JwtUtil;
import com.footbase.service.YorumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Yorum controller'ı
 * Yorum işlemleri endpoint'lerini içerir
 */
@RestController
@RequestMapping("/api/matches/comments")
@CrossOrigin(origins = "http://localhost:3000")
public class YorumController {

    @Autowired
    private YorumService yorumService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private KullaniciRepository kullaniciRepository;

    /**
     * Kullanıcı ID'sini JWT token'dan alır
     * @param request HTTP request (JWT token içerir)
     * @return Kullanıcı ID'si veya null
     */
    private Long getKullaniciIdFromRequest(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.getKullaniciEmailFromToken(token);
                return kullaniciRepository.findByEmail(email)
                        .map(k -> k.getId())
                        .orElse(null);
            }
        } catch (Exception e) {
            // Token yoksa veya geçersizse null döndür
        }
        return null;
    }

    /**
     * Yorumu günceller
     * @param id Yorum ID'si
     * @param yorumBilgileri Yeni yorum mesajı içeren map
     * @param request HTTP request (JWT token içerir)
     * @return Güncellenmiş yorum
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> yorumGuncelle(@PathVariable Long id, @RequestBody Map<String, String> yorumBilgileri, HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromRequest(request);
            if (kullaniciId == null) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            
            String mesaj = yorumBilgileri.get("message");
            if (mesaj == null || mesaj.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Yorum mesajı gereklidir"));
            }
            
            return ResponseEntity.ok(yorumService.yorumGuncelle(id, kullaniciId, mesaj.trim()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    /**
     * Yorumu siler
     * @param id Yorum ID'si
     * @param request HTTP request (JWT token içerir)
     * @return Başarı mesajı
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> yorumSil(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromRequest(request);
            if (kullaniciId == null) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            
            yorumService.yorumSil(id, kullaniciId);
            return ResponseEntity.ok(Map.of("mesaj", "Yorum başarıyla silindi"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    /**
     * Yorumu beğenir veya beğeniyi kaldırır
     * @param id Yorum ID'si
     * @param request HTTP request (JWT token içerir)
     * @return Beğeni durumu
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<?> yorumBegen(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromRequest(request);
            if (kullaniciId == null) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            
            boolean begenildi = yorumService.yorumBegen(id, kullaniciId);
            return ResponseEntity.ok(Map.of("begenildi", begenildi));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }
}


