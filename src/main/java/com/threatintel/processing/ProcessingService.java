package com.threatintel.processing;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final IOCRepository iocRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 2.0))
    @KafkaListener(topics = "extracted-ips", groupId = "processing-group")
    public void processIP(String ip) {
        if (isValidIP(ip)) {
            IOCEntity ioc = new IOCEntity();
            ioc.setValue(ip);
            ioc.setType("IP");
            ioc.setSource("AbuseIPDB");
            iocRepository.save(ioc);

            // Ranking ke liye bhejo
            kafkaTemplate.send("iocs-to-rank", ip + "|IP");
            System.out.println("IP processed and saved: " + ip);
        }
    }

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 2.0))
    @KafkaListener(topics = "extracted-domains", groupId = "processing-group")
    public void processDomain(String domain) {
        if (isValidDomain(domain)) {
            IOCEntity ioc = new IOCEntity();
            ioc.setValue(domain);
            ioc.setType("DOMAIN");
            ioc.setSource("AlienVault");
            iocRepository.save(ioc);

            kafkaTemplate.send("iocs-to-rank", domain + "|DOMAIN");
            System.out.println("Domain processed and saved: " + domain);
        }
    }

    private boolean isValidIP(String ip) {
        return ip != null && ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
    }

    private boolean isValidDomain(String domain) {
        return domain != null && domain.contains(".") && domain.length() > 3;
    }
}
