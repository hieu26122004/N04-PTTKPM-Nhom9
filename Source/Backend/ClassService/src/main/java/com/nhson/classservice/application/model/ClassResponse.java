package com.nhson.classservice.application.model;
import java.time.LocalDateTime;

public record ClassResponse(String classId,String className,String description,LocalDateTime createdDate){}