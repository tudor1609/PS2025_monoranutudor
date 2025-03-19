package com.example.demo.service;

import com.example.demo.dto.commentdto.CommentDTO;
import com.example.demo.dto.commentdto.CommentViewDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentViewDTO createComment(CommentDTO commentDTO, String userName) {
        User author = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(commentDTO.getPostId()).orElseThrow();
        Comment comment = Comment.builder()
                .text(commentDTO.getText())
                .image(decodeBase64(commentDTO.getBase64Image()))
                .publishedAt(LocalDateTime.now())
                .author(author)
                .post(post)
                .build();
        comment = commentRepository.save(comment);
        return mapToViewDTO(comment);
    }

    public List<CommentViewDTO> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post).stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    public List<CommentViewDTO> getCommentsByAuthor(User author) {
        return commentRepository.findByAuthor(author).stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    public CommentViewDTO updateComment(Long id, CommentDTO commentDTO, String userName) {
        User author = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = commentRepository.findById(id).orElseThrow();
        if (!comment.getAuthor().equals(author)) {
            throw new RuntimeException("You can only edit your own comments.");
        }
        comment.setText(commentDTO.getText());
        if (commentDTO.getBase64Image() != null) {
            comment.setImage(decodeBase64(commentDTO.getBase64Image()));
        }
        return mapToViewDTO(commentRepository.save(comment));
    }

    public void deleteComment(Long id, String userName) {
        User author = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = commentRepository.findById(id).orElseThrow();
        if (!comment.getAuthor().equals(author)) {
            throw new RuntimeException("You can only delete your own comments.");
        }
        commentRepository.delete(comment);
    }

    private byte[] decodeBase64(String base64) {
        return base64 != null ? Base64.getDecoder().decode(base64) : null;
    }

    private CommentViewDTO mapToViewDTO(Comment comment) {
        return CommentViewDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .base64Image(comment.getImage() != null ? Base64.getEncoder().encodeToString(comment.getImage()) : null)
                .publishedAt(comment.getPublishedAt())
                .authorUsername(comment.getAuthor().getName())
                .build();
    }
}
