package com.footbase.controller;

import com.footbase.entity.Bildirim;
import com.footbase.security.JwtUtil;
import com.footbase.service.BildirimServisi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Bildirim Controller
 * 
 * Bildirim API endpoint'lerini yönetir.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class BildirimController {
    
    @Autowired
    private BildirimServisi bildirimServisi;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * JWT token'dan kullanıcı ID'sini alır
     */
    private Long getKullaniciIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getKullaniciIdFromToken(token);
        }
        return null;
    }
    
    /**
     * Kullanıcının tüm bildirimlerini getirir
     * GET /api/notifications
     */
    @GetMapping
    public ResponseEntity<?> tumBildirimleriGetir(HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            List<Bildirim> bildirimler = bildirimServisi.kullaniciBildirimleriGetir(kullaniciId);
            return ResponseEntity.ok(bildirimler);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
    
    /**
     * Okunmamış bildirimleri getirir
     * GET /api/notifications/unread
     */
    @GetMapping("/unread")
    public ResponseEntity<?> okunmamisBildirimleriGetir(HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            List<Bildirim> bildirimler = bildirimServisi.okunmamisBildirimleriGetir(kullaniciId);
            return ResponseEntity.ok(bildirimler);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
    
    /**
     * Okunmamış bildirim sayısını döndürür
     * GET /api/notifications/unread/count
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> okunmamisSayisi(HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            Long sayi = bildirimServisi.okunmamisBildirimSayisi(kullaniciId);
            return ResponseEntity.ok(Map.of("count", sayi));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
    
    /**
     * Son N bildirimi getirir
     * GET /api/notifications/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<?> sonBildirimler(HttpServletRequest request, 
                                            @RequestParam(defaultValue = "10") int limit) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            List<Bildirim> bildirimler = bildirimServisi.sonBildirimleriGetir(kullaniciId, limit);
            return ResponseEntity.ok(bildirimler);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
    
    /**
     * Bildirimi okundu olarak işaretler
     * PUT /api/notifications/:id/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> bildirimOkundu(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            bildirimServisi.bildirimOkunduIsaretle(id);
            return ResponseEntity.ok(Map.of("mesaj", "Bildirim okundu olarak işaretlendi"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
    
    /**
     * Tüm bildirimleri okundu işaretle
     * PUT /api/notifications/read-all
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> tumunuOkundu(HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            int guncellenenSayi = bildirimServisi.tumBildirimleriOkunduIsaretle(kullaniciId);
            return ResponseEntity.ok(Map.of("mesaj", guncellenenSayi + " bildirim okundu olarak işaretlendi"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
    
    /**
     * Bildirimi siler
     * DELETE /api/notifications/:id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> bildirimSil(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            bildirimServisi.bildirimSil(id);
            return ResponseEntity.ok(Map.of("mesaj", "Bildirim silindi"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
}

