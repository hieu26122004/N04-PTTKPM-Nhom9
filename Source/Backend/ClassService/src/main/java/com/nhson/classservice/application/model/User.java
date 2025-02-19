package com.nhson.classservice.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "class_users",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // cột này trong DB thường là BIGINT
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Class> classes;
}
