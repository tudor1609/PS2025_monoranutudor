package com.example.demo.controller;

import com.example.demo.dto.commentdto.CommentDTO;
import com.example.demo.dto.commentdto.CommentViewDTO;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentViewDTO> createComment(
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("postId") Long postId,
            @RequestHeader("X-User-Name") String userName) throws IOException {

        CommentDTO commentDTO = CommentDTO.builder()
                .text(text)
                .base64Image(image != null ? Base64.getEncoder().encodeToString(image.getBytes()) : null)
                .postId(postId)
                .build();

        return ResponseEntity.ok(commentService.createComment(commentDTO, userName));
    }


    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentViewDTO>> getCommentsByPost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        return ResponseEntity.ok(commentService.getCommentsByPost(post));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CommentViewDTO>> getCommentsByAuthor(@PathVariable Long authorId) {
        User author = new User();
        author.setId(authorId);
        return ResponseEntity.ok(commentService.getCommentsByAuthor(author));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentViewDTO> updateComment(@PathVariable Long id,
                                                        @RequestBody CommentDTO commentDTO,
                                                        @RequestHeader("X-User-Name") String userName) {
        return ResponseEntity.ok(commentService.updateComment(id, commentDTO, userName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,
                                              @RequestHeader("X-User-Name") String userName) {
        commentService.deleteComment(id, userName);
        return ResponseEntity.noContent().build();
    }
}
