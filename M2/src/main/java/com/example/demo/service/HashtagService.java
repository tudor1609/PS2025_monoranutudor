package com.example.demo.service;

import com.example.demo.dto.hashtagdto.HashtagDTO;
import com.example.demo.entity.Hashtag;
import com.example.demo.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public HashtagDTO createHashtag(HashtagDTO hashtagDTO) {
        Hashtag hashtag = Hashtag.builder().name(hashtagDTO.getName()).build();
        hashtag = hashtagRepository.save(hashtag);
        return mapToDTO(hashtag);
    }

    public List<HashtagDTO> getAllHashtags() {
        return hashtagRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private HashtagDTO mapToDTO(Hashtag hashtag) {
        return HashtagDTO.builder()
                .name(hashtag.getName())
                .build();
    }
}
