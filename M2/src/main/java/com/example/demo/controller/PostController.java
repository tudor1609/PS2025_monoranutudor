package com.example.demo.controller;

import com.example.demo.dto.postdto.PostDTO;
import com.example.demo.dto.postdto.PostViewDTO;
import com.example.demo.entity.User;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostViewDTO> createPost(
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "hashtags", required = false) List<String> hashtags,
            @RequestHeader("X-User-Name") String userName) throws IOException {

        PostDTO postDTO = PostDTO.builder()
                .text(text)
                .base64Image(image != null ? Base64.getEncoder().encodeToString(image.getBytes()) : null)
                .hashtags(hashtags)
                .build();

        return ResponseEntity.ok(postService.createPost(postDTO, userName));
    }


    @GetMapping
    public ResponseEntity<List<PostViewDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/hashtag/{name}")
    public ResponseEntity<List<PostViewDTO>> getPostsByHashtag(@PathVariable String name) {
        return ResponseEntity.ok(postService.getPostsByHashtag(name));
    }

    @GetMapping("/text/{text}")
    public ResponseEntity<List<PostViewDTO>> getPostsByText(@PathVariable String text) {
        return ResponseEntity.ok(postService.getPostsByText(text));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostViewDTO>> getPostsByAuthor(@PathVariable Long authorId) {
        User author = new User();
        author.setId(authorId);
        return ResponseEntity.ok(postService.getPostsByAuthor(author));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostViewDTO> updatePost(@PathVariable Long id,
                                                  @RequestBody PostDTO postDTO,
                                                  @RequestHeader("X-User-Name") String userName) {
        return ResponseEntity.ok(postService.updatePost(id, postDTO, userName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id,
                                           @RequestHeader("X-User-Name") String userName) {
        postService.deletePost(id, userName);
        return ResponseEntity.noContent().build();
    }
}
