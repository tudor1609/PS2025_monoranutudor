package com.example.demo.repository;

import com.example.demo.entity.Friendship;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByUserAndStatus(User user, String status);

    List<Friendship> findByFriendAndStatus(User friend, String status);

    Optional<Friendship> findByUserAndFriend(User user, User friend);
}
