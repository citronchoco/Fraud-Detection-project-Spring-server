package com.fraud.detection.controllers;

import com.fraud.detection.dto.FraudResponseDTO;
import com.fraud.detection.enums.FraudStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TestController {
    @GetMapping("/call-python")
    public String callPython() {
        String url =  "http://localhost:8000/test";

        RestTemplate restTemplate = new RestTemplate();


        return restTemplate.getForObject(url, String.class);
    }

    @PostMapping("/upload-testImage")
    public String uploadTestImage(@RequestParam("file") MultipartFile file) {

        System.out.println("컨트롤러 진입 성공!");
        String url = "http://localhost:8000/test";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        FraudResponseDTO result = restTemplate.postForObject(url, requestEntity, FraudResponseDTO.class);

        System.out.println("이름: " + result.getFilename());
        System.out.println("메시지: " + result.getDescription());
        System.out.println("상태: " + result.getStatus());
        System.out.println("점수: " + result.getFraudScore());

        if (result.getStatus() == FraudStatus.FRAUD) {
            return "경고: 사기 이미지가 감지되었습니다";
        } else if (result.getStatus() == FraudStatus.SUSPICIOUS) {
            return "주의: 의심스러운 이미지입니다.";
        } else {
            return "정상: 깨끗한 이미지입니다.";
        }
    }
}
