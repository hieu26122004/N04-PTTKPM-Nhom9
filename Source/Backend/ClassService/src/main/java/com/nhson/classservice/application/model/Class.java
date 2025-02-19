package com.nhson.classservice.application.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "classes",
        indexes = {
                @Index(name = "idx_class_id", columnList = "class_id"),
                @Index(name = "idx_class_name", columnList = "class_name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Class implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "class_id", nullable = false, unique = true)
    private String classId;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "class_participants",
            joinColumns = @JoinColumn(name = "class_id", referencedColumnName = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List<User> participants;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", referencedColumnName = "class_id")
    @JsonManagedReference
    private List<Material> materials;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", referencedColumnName = "class_id")
    @JsonManagedReference
    private List<Announcement> announcements;
}
