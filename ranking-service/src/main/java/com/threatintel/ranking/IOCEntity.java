package com.threatintel.ranking;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "iocs")
@Data
public class IOCEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;
    private String type;
    private String source;
    private Double severityScore;
    private String status;
    private LocalDateTime createdAt;
}
