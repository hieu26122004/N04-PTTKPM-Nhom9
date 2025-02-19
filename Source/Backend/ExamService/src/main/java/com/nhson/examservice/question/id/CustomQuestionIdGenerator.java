package com.nhson.examservice.question.id;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

@Component
public class CustomQuestionIdGenerator implements IdentifierGenerator {
    private final SnowflakeIdGenerator questionIdGenerator;

    public CustomQuestionIdGenerator(SnowflakeIdGenerator questionIdGenerator) {
        this.questionIdGenerator = questionIdGenerator;
    }


    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return String.valueOf(questionIdGenerator.next());
    }
}
