package com.footbase.controller;

import com.footbase.entity.Mac;
import com.footbase.entity.MacOyuncuOlaylari;
import com.footbase.entity.Oyuncu;
import com.footbase.patterns.command.dto.MacSonlandirDTO;
import com.footbase.patterns.command.dto.SkorGirisiDTO;
import com.footbase.patterns.command.service.MacCommandService;
import com.footbase.repository.MacOyuncuOlaylariRepository;
import com.footbase.repository.MacRepository;
import com.footbase.repository.OyuncuRepository;
import com.footbase.security.JwtUtil;
import com.footbase.service.MacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Editor controller'ı
 * Editor işlemleri endpoint'lerini içerir
 */
@RestController
@RequestMapping("/api/editor")
@CrossOrigin(origins = "http://localhost:3000")
public class EditorController {

    @Autowired
    private MacService macService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private MacRepository macRepository;
    
    @Autowired
    private MacOyuncuOlaylariRepository macOyuncuOlaylariRepository;
    
    @Autowired
    private OyuncuRepository oyuncuRepository;

    @Autowired
    private MacCommandService macCommandService;

    /**
     * JWT token'dan kullanıcı ID'sini alır
     * @param request HTTP request
     * @return Kullanıcı ID'si
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
     * Editor tarafından yeni maç oluşturur
     * @param mac Maç bilgileri
     * @param request HTTP request (JWT token içerir)
     * @return Oluşturulan maç
     */
    @PostMapping("/matches")
    public ResponseEntity<?> macOlustur(@RequestBody Mac mac, HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            Mac olusturulanMac = macService.editorMacOlustur(mac, editorId);
            return ResponseEntity.ok(olusturulanMac);
        } catch (RuntimeException e) {
            // Runtime exception'ları 400 Bad Request olarak döndür
            e.printStackTrace(); // Log için
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            // Diğer exception'ları 500 Internal Server Error olarak döndür
            e.printStackTrace(); // Log için
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Maç oluşturulurken bir hata oluştu";
            }
            return ResponseEntity.internalServerError().body(Map.of("hata", errorMessage));
        }
    }

    /**
     * Editörün eklediği maçları getirir (ONAY_BEKLIYOR ve REDDEDILDI)
     * @param request HTTP request (JWT token içerir)
     * @return Editörün maçları listesi
     */
    @GetMapping("/matches/my-matches")
    public ResponseEntity<?> editorMaclariniGetir(HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("Editör " + editorId + " için maçlar getiriliyor");
            java.util.List<Mac> editorMaclari = macService.editorMaclariniGetir(editorId);
            System.out.println("Dönen maç sayısı: " + (editorMaclari != null ? editorMaclari.size() : 0));
            return ResponseEntity.ok(editorMaclari != null ? editorMaclari : java.util.Collections.emptyList());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", "Maçlar getirilirken bir hata oluştu: " + e.getMessage()));
        }
    }
    
    /**
     * Maç skorunu günceller
     * PUT /api/editor/matches/{id}/score
     */
    @PutMapping("/matches/{id}/score")
    public ResponseEntity<?> skorGuncelle(@PathVariable Long id, 
                                          @RequestBody Map<String, Integer> skorlar,
                                          HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            
            Integer evSahibiSkor = skorlar.get("evSahibiSkor");
            Integer deplasmanSkor = skorlar.get("deplasmanSkor");
            
            if (evSahibiSkor == null || deplasmanSkor == null) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Skorlar eksik"));
            }
            
            // MacTakimlari'den skorları güncelle
            macService.macSkorGuncelle(id, evSahibiSkor, deplasmanSkor, editorId);
            
            System.out.println("✓ Skor güncellendi: Mac ID=" + id + ", Skor=" + evSahibiSkor + "-" + deplasmanSkor);
            return ResponseEntity.ok(Map.of("mesaj", "Skor başarıyla güncellendi"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
    
    /**
     * Maça olay ekler (Gol, Sarı Kart, Kırmızı Kart)
     * POST /api/editor/matches/{id}/events
     */
    @PostMapping("/matches/{id}/events")
    public ResponseEntity<?> olayEkle(@PathVariable Long id,
                                      @RequestBody Map<String, Object> olayData,
                                      HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            
            Mac mac = macRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maç bulunamadı"));
            
            Long oyuncuId = Long.valueOf(olayData.get("oyuncuId").toString());
            String olayTuru = olayData.get("olayTuru").toString();
            
            Oyuncu oyuncu = oyuncuRepository.findById(oyuncuId)
                .orElseThrow(() -> new RuntimeException("Oyuncu bulunamadı"));
            
            // Olay oluştur
            MacOyuncuOlaylari olay = new MacOyuncuOlaylari();
            olay.setMac(mac);
            olay.setOyuncu(oyuncu);
            olay.setOlayTuru(olayTuru);
            
            macOyuncuOlaylariRepository.save(olay);
            
            System.out.println("✓ Olay eklendi: " + olayTuru + " - Oyuncu: " + oyuncu.getAd());
            return ResponseEntity.ok(Map.of("mesaj", olayTuru + " başarıyla eklendi"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
    
    /**
     * Maçı başlatır
     * @param id Maç ID'si
     * @param request HTTP request (JWT token içerir)
     * @return Başlatılan maç
     */
    @PostMapping("/matches/{id}/start")
    public ResponseEntity<?> macBaslat(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            Mac baslatilmasMac = macService.macBaslat(id, editorId);
            return ResponseEntity.ok(baslatilmasMac);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", "Maç başlatılırken bir hata oluştu: " + e.getMessage()));
        }
    }
    
    /**
     * Maçı sonuçlandırır
     * @param id Maç ID'si
     * @param request HTTP request (JWT token içerir)
     * @return Sonuçlandırılan maç
     */
    @PostMapping("/matches/{id}/finish")
    public ResponseEntity<?> macSonuclandir(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            Mac sonuclananMac = macService.macSonuclandir(id, editorId);
            return ResponseEntity.ok(sonuclananMac);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", "Maç sonuçlandırılırken bir hata oluştu: " + e.getMessage()));
        }
    }

    // ========== MAÇ SKOR VE SONLANDIRMA İŞLEMLERİ (COMMAND PATTERN) ==========

    /**
     * Maça skor girişi yapar (Command Pattern)
     * Geri alınabilir (undo)
     * 
     * @param skorGirisiDTO Skor girişi bilgileri
     * @param request HTTP request (JWT token içerir)
     * @return İşlem sonucu
     */
    @PostMapping("/matches/score-command")
    public ResponseEntity<?> macSkorGirisiCommand(@RequestBody SkorGirisiDTO skorGirisiDTO, HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("⚽ Editör " + editorId + " skor girişi yapıyor (Command Pattern): " + skorGirisiDTO);
            Map<String, Object> sonuc = macCommandService.skorGirisiYap(skorGirisiDTO, editorId);
            
            if ((Boolean) sonuc.get("basarili")) {
                return ResponseEntity.ok(sonuc);
            } else {
                return ResponseEntity.badRequest().body(sonuc);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", "Skor girişi yapılırken bir hata oluştu: " + e.getMessage()));
        }
    }

    /**
     * Maçı sonlandırır (Command Pattern)
     * Skorları girer ve durumu günceller
     * Geri alınabilir (undo)
     * 
     * @param macSonlandirDTO Maç sonlandırma bilgileri
     * @param request HTTP request (JWT token içerir)
     * @return İşlem sonucu
     */
    @PostMapping("/matches/finish-command")
    public ResponseEntity<?> macSonlandirCommand(@RequestBody MacSonlandirDTO macSonlandirDTO, HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("🏁 Editör " + editorId + " maç sonlandırıyor (Command Pattern): " + macSonlandirDTO);
            Map<String, Object> sonuc = macCommandService.macSonlandir(macSonlandirDTO, editorId);
            
            if ((Boolean) sonuc.get("basarili")) {
                return ResponseEntity.ok(sonuc);
            } else {
                return ResponseEntity.badRequest().body(sonuc);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", "Maç sonlandırılırken bir hata oluştu: " + e.getMessage()));
        }
    }

    /**
     * Son işlemi geri alır (undo)
     * Command Pattern'ın undo özelliği
     * 
     * @param request HTTP request (JWT token içerir)
     * @return İşlem sonucu
     */
    @PostMapping("/matches/undo")
    public ResponseEntity<?> sonIslemGeriAl(HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("🔄 Editör " + editorId + " son işlemi geri alıyor...");
            Map<String, Object> sonuc = macCommandService.sonIslemGeriAl(editorId);
            
            if ((Boolean) sonuc.get("basarili")) {
                return ResponseEntity.ok(sonuc);
            } else {
                return ResponseEntity.badRequest().body(sonuc);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", "İşlem geri alınırken bir hata oluştu: " + e.getMessage()));
        }
    }

    /**
     * İşlem geçmişini getirir
     * 
     * @param request HTTP request (JWT token içerir)
     * @return İşlem geçmişi
     */
    @GetMapping("/matches/history")
    public ResponseEntity<?> islemGecmisiGetir(HttpServletRequest request) {
        try {
            Long editorId = getKullaniciIdFromToken(request);
            if (editorId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("📚 Editör " + editorId + " işlem geçmişini getiriyor...");
            Map<String, Object> sonuc = macCommandService.islemGecmisiGetir();
            return ResponseEntity.ok(sonuc);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("hata", "İşlem geçmişi getirilirken bir hata oluştu: " + e.getMessage()));
        }
    }
}

