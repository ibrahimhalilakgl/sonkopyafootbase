package com.footbase.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT token işlemlerini yöneten yardımcı sınıf
 */
@Component
public class JwtUtil {

    /**
     * JWT token için kullanılacak gizli anahtar
     */
    @Value("${jwt.gizli-anahtar}")
    private String gizliAnahtar;

    /**
     * Token geçerlilik süresi (milisaniye cinsinden)
     */
    @Value("${jwt.gecerlilik-suresi}")
    private Long gecerlilikSuresi;

    /**
     * Gizli anahtardan SecretKey oluşturur
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = gizliAnahtar.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Token'dan kullanıcı adını (email) çıkarır
     * @param token JWT token
     * @return Kullanıcı email'i
     */
    public String getKullaniciEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Token'dan kullanıcı ID'sini çıkarır
     * @param token JWT token
     * @return Kullanıcı ID'si
     */
    public Long getKullaniciIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object kullaniciIdObj = claims.get("kullaniciId");
        if (kullaniciIdObj instanceof Integer) {
            return ((Integer) kullaniciIdObj).longValue();
        } else if (kullaniciIdObj instanceof Long) {
            return (Long) kullaniciIdObj;
        }
        return null;
    }

    /**
     * Token'dan belirli bir claim'i çıkarır
     * @param token JWT token
     * @param claimsResolver Claim çözümleyici fonksiyon
     * @return Claim değeri
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Token'dan tüm claim'leri çıkarır
     * @param token JWT token
     * @return Tüm claim'ler
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Token'ın geçerliliğini kontrol eder
     * @param token JWT token
     * @return Token geçerliyse true, değilse false
     */
    public Boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Token'ın süresinin dolup dolmadığını kontrol eder
     * @param token JWT token
     * @return Token süresi dolmuşsa true, dolmamışsa false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Token'dan son kullanma tarihini çıkarır
     * @param token JWT token
     * @return Son kullanma tarihi
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Kullanıcı için JWT token oluşturur
     * @param email Kullanıcı email'i
     * @param kullaniciId Kullanıcı ID'si
     * @return JWT token
     */
    public String generateToken(String email, Long kullaniciId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("kullaniciId", kullaniciId);
        return createToken(claims, email);
    }

    /**
     * JWT token oluşturur
     * @param claims Token'a eklenecek claim'ler
     * @param subject Token subject'i (genellikle email)
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + gecerlilikSuresi))
                .signWith(getSigningKey())
                .compact();
    }
}

