package com.threatintel.ingestion;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

    @GetMapping("/fetch")
    public String fetchNow() {
        // Manual trigger for testing
        ingestionService.fetchFromAbuseIPDB();
        ingestionService.fetchFromAlienVault();
        return "Manual data fetching started!";
    }

    @GetMapping("/test-data")
    public String sendTestData() {
        String mockData = "Found malicious IP: 1.2.3.4 and suspicious domain: example-malware.com";
        ingestionService.sendMockData(mockData);
        return "Mock data sent to Kafka! Check stats in 10 seconds.";
    }
}
