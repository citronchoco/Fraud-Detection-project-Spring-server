package com.fraud.detection.controllers;

import com.fraud.detection.dto.FraudResponseDTO;
import com.fraud.detection.enums.FraudStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@RestController
public class DetectionResultController {
    @Value("${supabase.storage.url}")
    private String supabaseUrl;

    @Value("${supabase.api.key}")
    private String apiKey;

    @PostMapping("/upload-images")
    public String uploadImage(@RequestParam("file") MultipartFile file) {

        // 클라이언트로부터 파일 받아오기


        // storage(버킷)에 저장하기
        try{
            // 엔드포인트 설정
            String verificationId = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            String finalSupabaseUrl = supabaseUrl + verificationId + "/"  + originalFilename;

            // HTTP 헤더 세팅
            HttpHeaders supabaseHeaders = new HttpHeaders();
            supabaseHeaders.set("Authorization", "Bearer " + apiKey);
            supabaseHeaders.set("apikey", apiKey);
            supabaseHeaders.setContentType(MediaType.valueOf(Objects.requireNonNull(file.getContentType())));

            HttpEntity<byte[]> supabaseEntity = new HttpEntity<>(file.getBytes(), supabaseHeaders);

            // Supabase API로 POST 요청 전송
            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.ResponseEntity<String> supabaseResponse = restTemplate.exchange(
                    finalSupabaseUrl,
                    HttpMethod.POST,
                    supabaseEntity,
                    String.class
            );

            System.out.println("Supabase 응답 결과: " + supabaseResponse.getBody());

        }catch (Exception e){
            return e.getMessage();
        }


        // 파이썬 서버로 데이터 넘기기
        String url = "http://localhost:8000/predict";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        FraudResponseDTO response = restTemplate.postForObject(url, requestEntity, FraudResponseDTO.class);


        if (response == null) return "";
        else if (response.getStatus() == FraudStatus.FRAUD) return "사기";
        else if (response.getStatus() == FraudStatus.NORMAL) return "정상";
        else if (response.getStatus() == FraudStatus.SUSPICIOUS) return "의심됨";
        return "something's wrong";
    }
}
