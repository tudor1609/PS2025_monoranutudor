package com.example.demo.service;

import com.example.demo.entity.Friendship;
import com.example.demo.entity.User;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public void sendFriendRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));


        Optional<Friendship> existingFriendship = friendshipRepository.findByUserAndFriend(sender, receiver);
        if (existingFriendship.isPresent()) {
            throw new RuntimeException("Friend request already sent or users are already friends");
        }

        Friendship friendship = Friendship.builder()
                .user(sender)
                .friend(receiver)
                .status("PENDING")
                .build();

        friendshipRepository.save(friendship);
    }


    public void acceptFriendRequest(Long friendshipId, Long acceptingUserId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!friendship.getFriend().getId().equals(acceptingUserId)) {
            throw new RuntimeException("Only the recipient can accept the friend request");
        }

        friendship.setStatus("ACCEPTED");
        friendshipRepository.save(friendship);


        Friendship reverseFriendship = Friendship.builder()
                .user(friendship.getFriend())
                .friend(friendship.getUser())
                .status("ACCEPTED")
                .build();
        friendshipRepository.save(reverseFriendship);
    }


    public List<User> getUserFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        List<User> friendsAsUser = friendshipRepository.findByUserAndStatus(user, "ACCEPTED")
                .stream()
                .map(Friendship::getFriend)
                .toList();

        List<User> friendsAsFriend = friendshipRepository.findByFriendAndStatus(user, "ACCEPTED")
                .stream()
                .map(Friendship::getUser)
                .toList();

        return Stream.concat(friendsAsUser.stream(), friendsAsFriend.stream()).distinct().toList();
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

}
