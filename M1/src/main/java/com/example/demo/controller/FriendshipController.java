package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;


    @PostMapping("/sendRequest")
    public ResponseEntity<String> sendRequest(@RequestParam Long friendId, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        friendshipService.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok("Friend request sent!");
    }


    @PostMapping("/acceptRequest")
    public ResponseEntity<String> acceptRequest(@RequestParam Long friendshipId, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        friendshipService.acceptFriendRequest(friendshipId, userId);
        return ResponseEntity.ok("Friend request accepted!");
    }


    @GetMapping("/list")
    public ResponseEntity<List<String>> getFriends(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<User> friends = friendshipService.getUserFriends(userId);
        List<String> friendNames = friends.stream().map(User::getName).toList();
        return ResponseEntity.ok(friendNames);
    }


    private Long getUserIdFromPrincipal(Principal principal) {

        User user = friendshipService.getUserByEmail(principal.getName());
        return user.getId();
    }
}
