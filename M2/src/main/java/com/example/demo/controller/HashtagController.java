package com.example.demo.controller;

import com.example.demo.dto.hashtagdto.HashtagDTO;
import com.example.demo.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @PostMapping
    public ResponseEntity<HashtagDTO> createHashtag(@RequestBody HashtagDTO hashtagDTO) {
        return ResponseEntity.ok(hashtagService.createHashtag(hashtagDTO));
    }

    @GetMapping
    public ResponseEntity<List<HashtagDTO>> getAllHashtags() {
        return ResponseEntity.ok(hashtagService.getAllHashtags());
    }
}
