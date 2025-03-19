package com.example.demo.controller;

import com.example.demo.dto.commentdto.CommentDTO;
import com.example.demo.dto.commentdto.CommentViewDTO;
import com.example.demo.security.JwtUtil;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentControllerProxy {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    private static final String M2_URL = "http://localhost:8082/comments";

    @PostMapping
    public ResponseEntity<CommentViewDTO> createComment(
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("postId") Long postId,
            @RequestHeader("Authorization") String token) throws IOException, java.io.IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String jwt = token.replace("Bearer ", "");
        String userEmail = jwtUtil.extractUsername(jwt);
        headers.set("X-User-Name", userEmail);
        headers.set("Authorization", token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        body.add("postId", postId);
        if (image != null) {
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String url = "http://localhost:8082/comments";

        ResponseEntity<CommentViewDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                CommentViewDTO.class
        );

        return response;
    }


    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentViewDTO>> getCommentsByPost(@PathVariable Long postId,
                                                                  @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = M2_URL + "/post/" + postId;
        ResponseEntity<CommentViewDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                CommentViewDTO[].class
        );
        return ResponseEntity.ok(Arrays.asList(response.getBody()));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CommentViewDTO>> getCommentsByAuthor(@PathVariable Long authorId,
                                                                    @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = M2_URL + "/author/" + authorId;
        ResponseEntity<CommentViewDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                CommentViewDTO[].class
        );
        return ResponseEntity.ok(Arrays.asList(response.getBody()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentViewDTO> updateComment(@PathVariable Long id,
                                                        @RequestBody CommentDTO commentDTO,
                                                        @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<CommentDTO> entity = new HttpEntity<>(commentDTO, headers);
        String url = M2_URL + "/" + id;
        ResponseEntity<CommentViewDTO> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                CommentViewDTO.class
        );
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,
                                              @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = M2_URL + "/" + id;
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        return ResponseEntity.noContent().build();
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jwt = token.replace("Bearer ", "");
        String userName = jwtUtil.extractUsername(jwt);
        headers.set("X-User-Name", userName);
        return headers;
    }
}
