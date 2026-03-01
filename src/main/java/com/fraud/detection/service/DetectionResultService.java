package com.fraud.detection.service;

import com.fraud.detection.dto.FraudResponseDTO;
import com.fraud.detection.entity.DetectionResult;
import com.fraud.detection.enums.FraudStatus;
import com.fraud.detection.repository.DetectionResultRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DetectionResultService {
    private final DetectionResultRepository repository;

    public DetectionResultService(DetectionResultRepository repository) {
        this.repository = repository;
    }

    @Value("${supabase.storage.url}")
    private String supabaseUrl;

    @Value("${supabase.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public FraudResponseDTO processDetection(List<MultipartFile> files, String scamType, String imageType) throws Exception {

        MultiValueMap<String, Object> pythonBody = new LinkedMultiValueMap<>();
        pythonBody.add("scamType", scamType);
        pythonBody.add("imageType", imageType);

        // 사진 url 모아둘 리스트
        List<String> uploadedUrls = new ArrayList<>();

        String verificationId = UUID.randomUUID().toString();

        for (MultipartFile file : files) {
            pythonBody.add("files", file.getResource());

            // 엔드포인트 설정
            String originalFilename = file.getOriginalFilename();
            String finalSupabaseUrl = supabaseUrl + verificationId + "/"  + originalFilename;

            // HTTP 헤더 세팅
            HttpHeaders supabaseHeaders = new HttpHeaders();
            supabaseHeaders.set("Authorization", "Bearer " + apiKey);
            supabaseHeaders.set("apikey", apiKey);
            supabaseHeaders.setContentType(MediaType.valueOf(Objects.requireNonNull(file.getContentType())));

            HttpEntity<byte[]> supabaseEntity = new HttpEntity<>(file.getBytes(), supabaseHeaders);

            // Supabase API로 POST 요청 전송
            org.springframework.http.ResponseEntity<String> supabaseResponse = restTemplate.exchange(
                    finalSupabaseUrl,
                    HttpMethod.POST,
                    supabaseEntity,
                    String.class
            );

//            if (supabaseResponse.getStatusCode().is2xxSuccessful()) uploadedUrls.add(finalSupabaseUrl);
//            else throw new RuntimeException("Error sending file: " + originalFilename);
        }

        // 파이썬 서버로 데이터 넘기기
        String pythonUrl = "http://localhost:8000/analyze";

        HttpHeaders pythonHeaders = new HttpHeaders();
        pythonHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> pythonEntity = new HttpEntity<>(pythonBody, pythonHeaders);

        // 결과
        FraudResponseDTO response = restTemplate.postForObject(pythonUrl, pythonEntity, FraudResponseDTO.class);
        FraudStatus status = (response != null) ? response.getStatus() : FraudStatus.NORMAL;

        // DB에 결과 저장하기
        DetectionResult result = new DetectionResult(verificationId, status);
        repository.save(result);

        return response;
    }
}
