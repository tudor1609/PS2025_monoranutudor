package com.example.demo.dto.postdto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private String text;
    private String base64Image;
    private List<String> hashtags;
    private Long authorId;
}
