package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.patterns.chain.HandlerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MacOnayChain {
    
    private static final Logger logger = LoggerFactory.getLogger(MacOnayChain.class);
    
    @Autowired
    private TarihKontrolHandler tarihKontrol;
    
    @Autowired
    private TakimKontrolHandler takimKontrol;
    
    @Autowired
    private SaatKontrolHandler saatKontrol;
    
    @Autowired
    private StadyumKontrolHandler stadyumKontrol;
    
    private MacOnayHandler chain;
    
    public MacOnayChain() {
        logger.info("⛓️ MacOnayChain oluşturuldu");
    }
    
    @jakarta.annotation.PostConstruct
    public void buildChain() {
        logger.info("🔗 Maç onay zinciri kuruluyor...");
        
        tarihKontrol.setNext(takimKontrol)
                    .setNext(saatKontrol)
                    .setNext(stadyumKontrol);
        
        chain = tarihKontrol;
        
        logger.info("✅ Zincir kuruldu: {}", chain.visualizeChain());
    }
    
    public HandlerResult validate(Mac mac) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("⚽ MAÇ ONAY SÜRECİ BAŞLIYOR");
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("Tarih: {} {}", mac.getTarih(), mac.getSaat());
        logger.info("Stadyum: {}", mac.getStadyum() != null ? mac.getStadyum() : "Belirtilmemiş");
        logger.info("───────────────────────────────────────────────────────");
        
        HandlerResult result = chain.handle(mac);
        
        logger.info("───────────────────────────────────────────────────────");
        if (result.isSuccess()) {
            logger.info("✅ ONAY BAŞARILI - Maç onaylandı");
        } else {
            logger.warn("❌ ONAY BAŞARISIZ - {}", result.getMessage());
        }
        logger.info("═══════════════════════════════════════════════════════\n");
        
        return result;
    }
    
    public boolean quickValidate(Mac mac) {
        return chain.handle(mac).isSuccess();
    }
    
    public String getChainVisualization() {
        return chain.visualizeChain();
    }
}
