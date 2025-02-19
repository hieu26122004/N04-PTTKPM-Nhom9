package com.nhson.classservice.application.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Announcement {
    @Id
    @Column(name = "announcement_id", nullable = false, unique = true)
    private String announcementId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "date", nullable = false)
    @CreatedDate
    private LocalDateTime date;
    @Column(name = "expires_at", nullable = true)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private AnnouncementType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    @JsonBackReference
    private Class clazz;

}

