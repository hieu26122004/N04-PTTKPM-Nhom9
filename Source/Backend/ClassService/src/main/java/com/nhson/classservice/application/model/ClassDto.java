package com.nhson.classservice.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassDto {
    private String className;
    private String description;
    private String teacherName;
}
