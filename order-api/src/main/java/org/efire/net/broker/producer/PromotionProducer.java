package org.efire.net.broker.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.efire.net.broker.message.PromotionMessage;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Component
@Slf4j
public class PromotionProducer {

    private KafkaTemplate<String, PromotionMessage> kafkaTemplate;

    public PromotionProducer(KafkaTemplate<String, PromotionMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ListenableFuture<SendResult<String, PromotionMessage>> handleMessage(PromotionMessage promotionMessage) {

        var promoRecord = new ProducerRecord<>("t.commodity.promotion",
                promotionMessage.getPromotionCode(), promotionMessage);
        var listenableFuture = this.kafkaTemplate.send(promoRecord);
        listenableFuture.addCallback(new KafkaSendCallback<>() {

            @Override
            public void onSuccess(SendResult<String, PromotionMessage> result) {
                handleSuccess(promotionMessage.getPromotionCode(), promotionMessage, result);
            }

            @Override
            public void onFailure(KafkaProducerException ex) {
                log.info("#####  On Failure #######"+ex.getMessage());
                ProducerRecord<String, PromotionMessage> failed = ex.getFailedProducerRecord();
                handleFailure(failed, ex);
            }

        });
        return listenableFuture;
    }

    private void handleFailure(ProducerRecord<String, PromotionMessage> failed, KafkaProducerException ex) {
        log.info("in handleFailure"+ex.getMessage());
        LocalDateTime triggerTime =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(failed.timestamp()),
                        TimeZone.getDefault().toZoneId());
        log.info("Error Sending the Message to topic {} and the exception is {} \n" +
                        "Payload: \n {} \n" +
                        "Triggered Date/Time: {} \n"
                , failed.topic(), ex.getMessage(), failed.value(), "triggerTime");
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(String name, PromotionMessage promotionMessage, SendResult<String, PromotionMessage> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}",
                name, promotionMessage, result.getRecordMetadata().partition());
    }

}
