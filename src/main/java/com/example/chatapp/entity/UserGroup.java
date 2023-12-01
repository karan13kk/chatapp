
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_group", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "group_id", "user_id" }, name = "unique_sender_receiver")
}, indexes = {
        @Index(name = "idx_sender_receiver", columnList = "user_id, group_id")
})
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @Column(name = "group_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(64)")
    private String groupId;

    @Column(name = "user_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(64)")
    private String userId;

    @Column(name = "last_msg_id", nullable = false)
    private Long lastMessageId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}