package com.nhson.examservice.config;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SnowFlakeConfig {

    @Value("${practice-makes-perfect.id.exam-generator-id}")
    private int examGeneratorId;

    @Value("${practice-makes-perfect.id.question-generator-id}")
    private int questionGeneratorId;

    @Bean(name = "examIdGenerator")
    public SnowflakeIdGenerator examIdGenerator() {
        return SnowflakeIdGenerator.createDefault(examGeneratorId);
    }

    @Bean(name = "questionIdGenerator")
    public SnowflakeIdGenerator questionIdGenerator() {
        return SnowflakeIdGenerator.createDefault(questionGeneratorId);
    }
}
