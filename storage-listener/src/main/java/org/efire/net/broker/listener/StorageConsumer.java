package org.efire.net.broker.listener;

import lombok.extern.slf4j.Slf4j;
import org.efire.net.broker.message.DiscountMessage;
import org.efire.net.broker.message.PromotionMessage;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@KafkaListener(topics = "t.commodity.promotion", containerFactory = "kafkaListenerContainerFactory")
public class StorageConsumer {

    @KafkaHandler
    public void onPromoMessage(PromotionMessage promotionMessage) {
        log.info("Promo received: {} ", promotionMessage.toString());
    }

    @KafkaHandler
    public void onDiscountMessage(DiscountMessage discountMessage) {
        log.info("Discount received: {} ",discountMessage.toString());
    }

    //NOTE: Make sure isDefault = true is define if you have (Object) method signature
    // Else, it will trigger an error such as 'Ambiguous methods for payload type'
    @KafkaHandler(isDefault = true)
    public void onDefaultMessage(Object object) {
        log.info("====>> Default <<====");
        log.info(object.toString());
    }
}
