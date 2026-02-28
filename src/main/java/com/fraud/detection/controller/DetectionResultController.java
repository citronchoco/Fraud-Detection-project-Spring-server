package com.fraud.detection.controller;

import com.fraud.detection.enums.FraudStatus;
import com.fraud.detection.service.DetectionResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor // 롬복에 이런 것도 있음 - 자동으로 DetectionResultService의 생성자 생성해줌
public class DetectionResultController {

    private final DetectionResultService detectionResultService;

    @PostMapping("/upload-images")
    public String uploadImage(@RequestParam("file") MultipartFile file) {

        // 클라이언트로부터 파일 받아오기

        try{
            FraudStatus status = detectionResultService.processDetection(file);

          if (status == FraudStatus.FRAUD) return "사기";
          else if (status == FraudStatus.NORMAL) return "정상";
          else if (status == FraudStatus.SUSPICIOUS) return "의심됨";

          return "something's wrong.";
        }catch (Exception e){
            return "에러 발생: " + e.getMessage();
        }
    }
}
