package com.fraud.detection.repository;

import com.fraud.detection.entity.DetectionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionResultRepository extends JpaRepository<DetectionResult, Long> {

}
