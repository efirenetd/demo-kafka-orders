package org.efire.net.broker.consumer;

import lombok.extern.slf4j.Slf4j;
import org.efire.net.broker.message.OrderReplyMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderReplyConsumer {

    @KafkaListener(topics = "t.commodity.order-reply")
    public void onMessage(@Payload OrderReplyMessage message) {
        log.info("Order reply received. {} ", message);
    }
}
