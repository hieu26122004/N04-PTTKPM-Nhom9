package com.nhson.userservice.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships")
public class Friendship {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

