package com.example.chatapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatapp.entity.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, String> {
    List<UserGroup> findByGroupId(String groupId);

    List<UserGroup> findByUserId(String userId);

    Optional<UserGroup> findByUserIdAndGroupId(String userId, String groupId);
}