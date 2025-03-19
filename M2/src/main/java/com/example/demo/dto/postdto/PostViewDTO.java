package com.example.demo.dto.postdto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostViewDTO {
    private Long id;
    private String text;
    private String base64Image;
    private LocalDateTime publishedAt;
    private String authorUsername;
    private List<String> hashtags;
}
