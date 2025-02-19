package com.nhson.examservice.queue;

import com.nhson.examservice.exam.repositories.ExamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQAppController {

    private final ExamRepository examRepository;

    public RabbitMQAppController(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "delete-class", durable = "true" ),
                    exchange = @Exchange(value = RabbitMQConfig.CLASS_EXCHANGE_NAME, type = "topic"),
                    key = "class.delete"
            )
    )
    public void processDeleteEvent(@Payload String message) {
        examRepository.deleteAllByClassId(message);
        log.info("Delete all exams with class id = " + message);
    }
}
