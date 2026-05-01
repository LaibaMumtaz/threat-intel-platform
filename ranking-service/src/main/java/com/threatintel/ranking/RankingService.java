package com.threatintel.ranking;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final IOCRepository iocRepository;
    private final RestTemplate restTemplate;

    @Value("${ABUSEIPDB_API_KEY}")
    private String abuseIpdbKey;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 2.0))
    @KafkaListener(topics = "iocs-to-rank", groupId = "ranking-group")
    public void rankIOC(String message) {
        try {
            String[] parts = message.split("\\|");
            String value = parts[0];
            String type = parts[1];

            double score = 0.0;

            if (type.equals("IP")) {
                score = getIPScore(value);
            } else {
                score = getDomainScore(value);
            }

            // Database mein update karo
            updateScore(value, score);
            System.out.println("Ranked " + value + " | Score: " + score);
        } catch (Exception e) {
            System.err.println("Error processing ranking message: " + e.getMessage());
            // Sirf fallback dikhane ke liye hum retry nahi kar rahe, balkay graceful degradation (Circuit Breaker) use kar rahe hain
            // throw new RuntimeException("Ranking failed, triggering retry...", e);
        }
    }

    private double getIPScore(String ip) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Key", abuseIpdbKey);
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                "https://api.abuseipdb.com/api/v2/check?ipAddress=" + ip,
                HttpMethod.GET,
                entity,
                String.class
            );

            // Score parse karo (simplified logic)
            String body = response.getBody();
            if (body != null && body.contains("abuseConfidenceScore")) {
                int start = body.indexOf("abuseConfidenceScore") + 23;
                int end = body.indexOf(",", start);
                if (end == -1) end = body.indexOf("}", start);
                return Double.parseDouble(body.substring(start, end).trim());
            }
        } catch (Exception e) {
            System.err.println("Error ranking IP from API (Rate Limit Hit): " + e.getMessage());
            System.out.println("Applying Fallback / Circuit Breaker for IP: " + ip);
            // Fallback logic for demonstration
            if (ip.startsWith("1.") || ip.startsWith("100.")) {
                return 85.0; // Mock Malicious
            } else {
                return 20.0; // Mock Safe
            }
        }
        return 0.0;
    }

    private double getDomainScore(String domain) {
        // Simple manual scoring for domains
        if (domain.contains("malware") || domain.contains("phish") || domain.contains("hack")) {
            return 85.0;
        }
        return 20.0;
    }

    private void updateScore(String value, double score) {
        iocRepository.findByValue(value).ifPresent(ioc -> {
            ioc.setSeverityScore(score);
            ioc.setStatus(score > 50 ? "MALICIOUS" : "SAFE");
            iocRepository.save(ioc);
        });
    }
}
