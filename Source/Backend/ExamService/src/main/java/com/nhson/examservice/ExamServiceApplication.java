package com.nhson.examservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ExamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamServiceApplication.class, args);
    }

}
