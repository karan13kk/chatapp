package com.example.chatapp.repository;

import com.example.chatapp.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    Optional<User> findByUserIdAndPasscode(String userId, String passcode);

    @Query("SELECT u.userName FROM User u")
    List<String> findAllUserName();
}