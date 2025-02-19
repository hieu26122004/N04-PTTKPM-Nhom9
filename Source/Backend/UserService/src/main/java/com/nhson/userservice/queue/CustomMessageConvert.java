package com.nhson.userservice.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhson.userservice.domain.ProfileReq;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

public class CustomMessageConvert implements MessageConverter {

    private final ObjectMapper objectMapper;

    public CustomMessageConvert(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        try {
            String json = objectMapper.writeValueAsString(o);
            Message message = new Message(json.getBytes(), messageProperties);
            return message;
        } catch (JsonProcessingException e) {
            throw new MessageConversionException("Error converting object to message", e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            String json = new String(message.getBody());
            ProfileReq req = objectMapper.readValue(json, ProfileReq.class);
            return req;
        } catch (JsonProcessingException e) {
            throw new MessageConversionException("Error converting message to object", e);
        }
    }
}
