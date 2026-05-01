package com.threatintel.extraction;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.regex.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExtractionService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // IP pattern
    private static final Pattern IP_PATTERN =
        Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");

    // Domain pattern
    private static final Pattern DOMAIN_PATTERN =
        Pattern.compile("\\b([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}\\b");

    @KafkaListener(topics = "raw-iocs", groupId = "extraction-group")
    public void extractIOCs(String rawData) {
        List<String> ips = extractIPs(rawData);
        List<String> domains = extractDomains(rawData);

        // IPs Kafka mein bhejo
        for (String ip : ips) {
            kafkaTemplate.send("extracted-ips", ip);
        }

        // Domains Kafka mein bhejo
        for (String domain : domains) {
            kafkaTemplate.send("extracted-domains", domain);
        }

        System.out.println("Extracted " + ips.size() + 
                          " IPs and " + domains.size() + " Domains from raw data.");
    }

    private List<String> extractIPs(String text) {
        List<String> ips = new ArrayList<>();
        Matcher matcher = IP_PATTERN.matcher(text);
        while (matcher.find()) {
            ips.add(matcher.group());
        }
        return ips;
    }

    private List<String> extractDomains(String text) {
        List<String> domains = new ArrayList<>();
        Matcher matcher = DOMAIN_PATTERN.matcher(text);
        while (matcher.find()) {
            domains.add(matcher.group());
        }
        return domains;
    }
}
