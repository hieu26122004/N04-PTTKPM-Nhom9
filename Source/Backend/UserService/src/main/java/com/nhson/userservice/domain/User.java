package com.nhson.userservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users_profile")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class User {
    @Id
    private String userId;

    @Column(name = "username")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "bio")
    private String bio;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Transient
    private List<String> roles;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Embedded
    private SecuritySettings securitySettings;

    @Embedded
    private Preferences preferences;

    @Embedded
    private UserStatistics userStatistics;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "friendships",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();

    public User(ProfileReq req) {
        this.userId = req.getUserId();
        this.userName = req.getUserName();
        this.email = req.getEmail();
        this.createdAt = req.getCreatedAt();
        this.lastLogin = req.getLastLogin();
        this.status = req.getStatus();
    }

}
