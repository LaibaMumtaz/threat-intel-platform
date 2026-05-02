package com.threatintel.processing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IOCRepository extends JpaRepository<IOCEntity, Long> {
    List<IOCEntity> findByType(String type);
    List<IOCEntity> findByStatus(String status);
    Optional<IOCEntity> findByValue(String value);
}
