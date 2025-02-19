package com.nhson.classservice.application.service;
import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdService {

    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final int GENERATOR_ID = 8;
    public SnowflakeIdService() {
        this.snowflakeIdGenerator = SnowflakeIdGenerator.createDefault(GENERATOR_ID);
    }
    public long generateId() {
        return snowflakeIdGenerator.next();
    }
}

