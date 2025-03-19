package com.example.demo.dto.commentdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
