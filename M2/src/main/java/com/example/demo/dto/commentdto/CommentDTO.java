package com.example.demo.dto.commentdto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private String text;
    private String base64Image;
    private Long authorId;
    private Long postId;
}
