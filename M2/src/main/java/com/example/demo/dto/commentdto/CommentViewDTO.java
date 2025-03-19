package com.example.demo.dto.commentdto;

import lombok.*;

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
