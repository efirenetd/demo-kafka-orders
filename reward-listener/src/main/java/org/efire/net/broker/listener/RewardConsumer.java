package org.efire.net.broker.listener;

import lombok.extern.slf4j.Slf4j;
import org.efire.net.broker.message.OrderMessage;
import org.efire.net.broker.message.OrderReplyMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RewardConsumer {

    @KafkaListener(topics = "t.commodity.order")
    @SendTo("t.commodity.order-reply")
    public OrderReplyMessage onMessage(@Payload OrderMessage orderMessage,
                          @Header("supriseBonus") String supriseBonus,
                          //To retrieve all Headers
                          @Headers MessageHeaders messageHeaders) {
        log.info("Suprise Bonus: {}", supriseBonus);
        //To retrieve all Headers
        messageHeaders.keySet().forEach(key -> {
            Object value = messageHeaders.get(key);
            if (key.equals("supriseBonus")) {
                log.info("{} : {} ", key, new String((byte[]) value));
            }
            else {
                log.info("===> {} : {}", key, value);
            }
        });

        log.info("{} / 100 * {} * {}", supriseBonus, orderMessage.getPrice(), orderMessage.getQuantity());
        var bonusAmount = (Double.parseDouble(supriseBonus)/100) * orderMessage.getPrice().intValue() * orderMessage.getQuantity().intValue();
        log.info("Bonus Amount: {} ", bonusAmount);
        log.info("Reward was processed successfully. Sending order reply.");
        //TODO: Added Feature: Asynchronous Request/Reply
        var replyMessage = new OrderReplyMessage("Order has been processed with applied reward.");
        return replyMessage;
    }
}
