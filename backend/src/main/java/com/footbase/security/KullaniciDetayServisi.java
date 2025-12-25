package com.footbase.security;

import com.footbase.entity.Kullanici;
import com.footbase.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Spring Security için kullanıcı detay servisi
 * Kullanıcı bilgilerini yükler ve Spring Security'ye sağlar
 */
@Service
public class KullaniciDetayServisi implements UserDetailsService {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    /**
     * E-posta adresine göre kullanıcı detaylarını yükler
     * @param email Kullanıcı e-posta adresi
     * @return Kullanıcı detayları
     * @throws UsernameNotFoundException Kullanıcı bulunamazsa
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Kullanici kullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        return new User(
                kullanici.getEmail(),
                kullanici.getSifre(),
                new ArrayList<>() // Roller (şimdilik boş)
        );
    }
}



