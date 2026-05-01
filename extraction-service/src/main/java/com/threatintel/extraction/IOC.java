package com.threatintel.extraction;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IOC {
    private String value;
    private String type; // "IP" or "DOMAIN"
    private String source;
}
