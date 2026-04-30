package com.threatintel.query;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/iocs")
@RequiredArgsConstructor
public class QueryController {

    private final IOCRepository iocRepository;

    @GetMapping("/all")
    public List<IOCEntity> getAllIOCs() {
        return iocRepository.findAll();
    }

    @GetMapping("/type/{type}")
    public List<IOCEntity> getByType(@PathVariable String type) {
        return iocRepository.findByType(type);
    }

    @GetMapping("/malicious")
    public List<IOCEntity> getMalicious() {
        return iocRepository.findByStatus("MALICIOUS");
    }

    @GetMapping("/check/{value}")
    public IOCEntity checkIOC(@PathVariable String value) {
        return iocRepository.findByValue(value).orElse(null);
    }

    @GetMapping("/stats")
    public String getStats() {
        long total = iocRepository.count();
        long malicious = iocRepository.findByStatus("MALICIOUS").size();
        long safe = iocRepository.findByStatus("SAFE").size();

        return "Total: " + total + " | Malicious: " + malicious + " | Safe: " + safe;
    }
}
