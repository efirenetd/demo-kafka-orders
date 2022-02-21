package org.efire.net.broker.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.efire.net.broker.message.OrderMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PatternConsumer {

    private ObjectMapper objectMapper;

    public PatternConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "t.commodity.order")
    public void onMessage(@Payload OrderMessage orderMessage) throws JsonProcessingException {
        log.info("--xxxxx Received OrderMessage xxxxxx--");
        log.info("OrderNumber: {} ", orderMessage.getOrderNumber());
        var totalAmount = getTotalAmount(orderMessage);
        //TODO: For now let the information be display in the log. Will implement feature to persist the record in a database.
        log.info("Processing Order: {}, with Total Amount of {}", orderMessage.getOrderNumber(), totalAmount);

    }

    private double getTotalAmount(OrderMessage orderMessage) {
        return orderMessage.getQuantity() * orderMessage.getPrice();
    }

}
