package com.threatintel.ingestion;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngestionService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;

    @Value("${ABUSEIPDB_API_KEY}")
    private String abuseIpdbKey;

    @Value("${ALIENVAULT_API_KEY}")
    private String alienVaultKey;

    // AbuseIPDB se data fetch karo har din
    @Scheduled(fixedRate = 86400000)
    public void fetchFromAbuseIPDB() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Key", abuseIpdbKey);
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                "https://api.abuseipdb.com/api/v2/blacklist?limit=100",
                HttpMethod.GET,
                entity,
                String.class
            );

            // Kafka mein "raw-iocs" topic pe data bhejo
            kafkaTemplate.send("raw-iocs", response.getBody());
            System.out.println("AbuseIPDB data fetched and sent to Kafka!");
        } catch (Exception e) {
            System.err.println("Error fetching from AbuseIPDB: " + e.getMessage());
        }
    }

    // AlienVault se data fetch karo har ghante
    @Scheduled(fixedRate = 3600000)
    public void fetchFromAlienVault() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-OTX-API-KEY", alienVaultKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                "https://otx.alienvault.com/api/v1/pulses/subscribed",
                HttpMethod.GET,
                entity,
                String.class
            );

            // Kafka mein "raw-iocs" topic pe data bhejo
            kafkaTemplate.send("raw-iocs", response.getBody());
            System.out.println("AlienVault data fetched and sent to Kafka!");
        } catch (Exception e) {
            System.err.println("Error fetching from AlienVault: " + e.getMessage());
        }
    }

    public void sendMockData(String data) {
        kafkaTemplate.send("raw-iocs", data);
        System.out.println("Mock data sent to Kafka: " + data);
    }
}
