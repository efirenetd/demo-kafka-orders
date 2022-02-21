package org.efire.net.broker.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.efire.net.broker.message.DiscountMessage;
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
public class DiscountProducer {

    private KafkaTemplate<String, DiscountMessage> kafkaTemplate;

    public DiscountProducer(KafkaTemplate<String, DiscountMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ListenableFuture<SendResult<String, DiscountMessage>> handleMessage(DiscountMessage discountMessage) {
        var discountRecord = new ProducerRecord<>("t.commodity.promotion",
                discountMessage.getDiscountCode(), discountMessage);
        var listenableFuture = this.kafkaTemplate.send(discountRecord);
        listenableFuture.addCallback(new KafkaSendCallback<>() {

            @Override
            public void onSuccess(SendResult<String, DiscountMessage> result) {
                handleSuccess(discountMessage.getDiscountCode(), discountMessage, result);
            }

            @Override
            public void onFailure(KafkaProducerException ex) {
                log.info("#####  On Failure #######"+ex.getMessage());
                ProducerRecord<String, DiscountMessage> failed = ex.getFailedProducerRecord();
                handleFailure(failed, ex);
            }
        });
        return listenableFuture;
    }

    private void handleFailure(ProducerRecord<String, DiscountMessage> failed, KafkaProducerException ex) {
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

    private void handleSuccess(String name, DiscountMessage discountMessage, SendResult<String, DiscountMessage> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {}, partition is {}",
                name, discountMessage, result.getRecordMetadata().partition());
    }

}
