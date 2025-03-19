package com.example.demo.controller;

import com.example.demo.dto.postdto.PostDTO;
import com.example.demo.dto.postdto.PostViewDTO;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostControllerProxy {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    private static final String M2_URL = "http://localhost:8082/posts";


    @PostMapping
    public ResponseEntity<PostViewDTO> createPost(
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "hashtags", required = false) List<String> hashtags,
            @RequestHeader("Authorization") String token) throws IOException, java.io.IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String jwt = token.replace("Bearer ", "");
        String userName = jwtUtil.extractUsername(jwt);
        headers.set("X-User-Name", userName);
        headers.set("Authorization", token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        if (image != null) {
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
        }
        if (hashtags != null) {
            for (String hashtag : hashtags) {
                body.add("hashtags", hashtag);
            }
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = "http://localhost:8082/posts";
        ResponseEntity<PostViewDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                PostViewDTO.class
        );
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostViewDTO> updatePost(@PathVariable Long id,
                                                  @RequestBody PostDTO postDTO,
                                                  @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<PostDTO> entity = new HttpEntity<>(postDTO, headers);
        String url = M2_URL + "/" + id;
        ResponseEntity<PostViewDTO> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                PostViewDTO.class
        );
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = M2_URL + "/" + id;
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hashtag/{name}")
    public ResponseEntity<List<PostViewDTO>> getPostsByHashtag(@PathVariable String name, @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = M2_URL + "/hashtag/" + name;
        ResponseEntity<PostViewDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                PostViewDTO[].class
        );
        return ResponseEntity.ok(Arrays.asList(response.getBody()));
    }

    @GetMapping("/text/{text}")
    public ResponseEntity<List<PostViewDTO>> getPostsByText(@PathVariable String text, @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = M2_URL + "/text/" + text;
        ResponseEntity<PostViewDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                PostViewDTO[].class
        );
        return ResponseEntity.ok(Arrays.asList(response.getBody()));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostViewDTO>> getPostsByAuthor(@PathVariable Long authorId, @RequestHeader("Authorization") String token) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = M2_URL + "/author/" + authorId;
        ResponseEntity<PostViewDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                PostViewDTO[].class
        );
        return ResponseEntity.ok(Arrays.asList(response.getBody()));
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
