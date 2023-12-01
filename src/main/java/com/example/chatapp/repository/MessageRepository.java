package com.example.chatapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.chatapp.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByGroupId(String groupId);

    @Query("SELECT m FROM Message m WHERE m.groupId=:groupId and id>:lastMessageId")
    List<Message> findByGroupIdAndMessageId(String groupId, Long lastMessageId);

    @Query("SELECT m FROM Message m WHERE m.groupId IN :groupIds")
    List<Message> findByGroupIdsIn(List<String> groupIds);
}