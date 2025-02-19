package com.nhson.classservice.application.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materials")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Material {
    @Id
    @Column(name = "material_id", nullable = false, unique = true)
    private String materialId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "url", nullable = false)
    private String url;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    @JsonBackReference
    private Class aClass;

}