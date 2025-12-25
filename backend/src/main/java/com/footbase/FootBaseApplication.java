package com.footbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FootBase uygulamasının ana sınıfı
 * Spring Boot uygulamasını başlatır
 */
@SpringBootApplication
public class FootBaseApplication {

    /**
     * Uygulamanın giriş noktası
     * @param args Komut satırı argümanları
     */
    public static void main(String[] args) {
        SpringApplication.run(FootBaseApplication.class, args);
    }
}



