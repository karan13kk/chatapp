package com.example.chatapp.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message", indexes = {
        @Index(name = "idx", columnList = "sender_id, group_id")
})
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @Column(name = "sender_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(64)")
    private String senderId;

    @Column(name = "sender_name", updatable = false, nullable = false)
    private String senderName;

    @Column(name = "group_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(64)")
    private String groupId;

    @Column(name = "text", updatable = false, nullable = false, columnDefinition = "TEXT")
    private String text;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}