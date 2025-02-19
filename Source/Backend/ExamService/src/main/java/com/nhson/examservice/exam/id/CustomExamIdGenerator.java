package com.nhson.examservice.exam.id;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomExamIdGenerator implements IdentifierGenerator {

    private final SnowflakeIdGenerator examIdGenerator;

    public CustomExamIdGenerator(SnowflakeIdGenerator examIdGenerator) {
        this.examIdGenerator = examIdGenerator;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return String.valueOf(examIdGenerator.next());
    }
}
