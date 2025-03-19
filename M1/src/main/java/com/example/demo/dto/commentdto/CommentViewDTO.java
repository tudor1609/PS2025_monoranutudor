package com.example.demo.dto.commentdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentViewDTO {
    private Long id;
    private String text;
    private String base64Image;
    private LocalDateTime publishedAt;
    private String authorUsername;
}
