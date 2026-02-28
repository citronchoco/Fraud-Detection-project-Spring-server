package com.fraud.detection.dto;

import com.fraud.detection.enums.FraudStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class FraudResponseDTO {
    private String filename;
    private String description;
    private FraudStatus status;
    private float fraudScore;
}
