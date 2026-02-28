package com.fraud.detection.entity;

import com.fraud.detection.enums.FraudStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class DetectionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String verificationId;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudStatus  status;

    public DetectionResult(String verificationId, String imageUrl, FraudStatus status) {
        this.verificationId = verificationId;
        this.imageUrl = imageUrl;
        this.status = status;
    }
}
