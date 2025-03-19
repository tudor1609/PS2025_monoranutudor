package com.example.demo.controller;

import com.example.demo.dto.hashtagdto.HashtagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/hashtags")
@RequiredArgsConstructor
public class HashtagControllerProxy {

    private final RestTemplate restTemplate;

    private static final String M2_URL = "http://localhost:8082/hashtags";

    @PostMapping
    public ResponseEntity<HashtagDTO> createHashtag(@RequestBody HashtagDTO hashtagDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HashtagDTO> entity = new HttpEntity<>(hashtagDTO, headers);
        ResponseEntity<HashtagDTO> response = restTemplate.exchange(
                M2_URL,
                HttpMethod.POST,
                entity,
                HashtagDTO.class
        );
        return response;
    }

    @GetMapping
    public ResponseEntity<List<HashtagDTO>> getAllHashtags() {
        ResponseEntity<HashtagDTO[]> response = restTemplate.getForEntity(M2_URL, HashtagDTO[].class);
        return ResponseEntity.ok(Arrays.asList(response.getBody()));
    }
}
