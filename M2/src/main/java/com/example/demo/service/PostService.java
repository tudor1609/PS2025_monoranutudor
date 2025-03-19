package com.example.demo.service;

import com.example.demo.dto.postdto.PostDTO;
import com.example.demo.dto.postdto.PostViewDTO;
import com.example.demo.entity.Hashtag;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.HashtagRepository;
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
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final UserRepository userRepository;

    public PostViewDTO createPost(PostDTO postDTO, String email) {
        User author = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        byte[] imageBytes = null;
        if (postDTO.getBase64Image() != null && !postDTO.getBase64Image().isEmpty()) {
            try {
                imageBytes = Base64.getDecoder().decode(postDTO.getBase64Image());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid base64 image data");
            }
        }

        Post post = Post.builder()
                .text(postDTO.getText())
                .image(imageBytes)
                .publishedAt(LocalDateTime.now())
                .author(author)
                .hashtags(processHashtags(postDTO.getHashtags()))
                .build();

        post = postRepository.save(post);
        return mapToViewDTO(post);
    }


    public List<PostViewDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .sorted((p1, p2) -> p2.getPublishedAt().compareTo(p1.getPublishedAt()))
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    public List<PostViewDTO> getPostsByHashtag(String hashtag) {
        return postRepository.findByHashtagName(hashtag).stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    public List<PostViewDTO> getPostsByText(String text) {
        return postRepository.findByTextContainingIgnoreCase(text).stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    public List<PostViewDTO> getPostsByAuthor(User author) {
        return postRepository.findByAuthor(author).stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    public PostViewDTO updatePost(Long id, PostDTO postDTO, String userName) {
        User author = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(id).orElseThrow();
        if (!post.getAuthor().equals(author)) {
            throw new RuntimeException("You can only edit your own posts.");
        }
        post.setText(postDTO.getText());
        if (postDTO.getBase64Image() != null) {
            post.setImage(decodeBase64(postDTO.getBase64Image()));
        }
        post.setHashtags(processHashtags(postDTO.getHashtags()));
        return mapToViewDTO(postRepository.save(post));
    }

    public void deletePost(Long id, String userName) {
        User author = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(id).orElseThrow();
        if (!post.getAuthor().equals(author)) {
            throw new RuntimeException("You can only delete your own posts.");
        }
        postRepository.delete(post);
    }

    private List<Hashtag> processHashtags(List<String> hashtags) {
        return hashtags.stream()
                .map(name -> hashtagRepository.findByName(name).orElseGet(() ->
                        hashtagRepository.save(Hashtag.builder().name(name).build())))
                .collect(Collectors.toList());
    }

    private byte[] decodeBase64(String base64) {
        return base64 != null ? Base64.getDecoder().decode(base64) : null;
    }

    private PostViewDTO mapToViewDTO(Post post) {
        return PostViewDTO.builder()
                .id(post.getId())
                .text(post.getText())
                .base64Image(post.getImage() != null ? Base64.getEncoder().encodeToString(post.getImage()) : null)
                .publishedAt(post.getPublishedAt())
                .authorUsername(post.getAuthor().getName())
                .hashtags(post.getHashtags().stream().map(Hashtag::getName).collect(Collectors.toList()))
                .build();
    }
}
