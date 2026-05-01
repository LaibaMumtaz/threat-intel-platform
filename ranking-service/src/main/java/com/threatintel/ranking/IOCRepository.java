package com.threatintel.ranking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IOCRepository extends JpaRepository<IOCEntity, Long> {
    Optional<IOCEntity> findByValue(String value);
}
