package com.example.chatapp.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "user_db")
public class User {
    @Id
    @Column(name = "user_id", updatable = false, nullable = false, unique = true, columnDefinition = "VARCHAR(64)")
    private String userId;

    @Column(name = "user_name", updatable = false, nullable = false)
    private String userName;

    @Column(name = "passcode", nullable = false, columnDefinition = "VARCHAR(64)")
    private String passcode;

    @Column(name = "is_logged_in", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isLoggedIn;

    @Column(name = "last_logged_in_time", nullable = true)
    private Timestamp lastLoggedInTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}