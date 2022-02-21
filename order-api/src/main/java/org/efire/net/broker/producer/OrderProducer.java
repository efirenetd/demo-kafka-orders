package org.efire.net.broker.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.efire.net.broker.message.OrderMessage;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Component
@Slf4j
public class OrderProducer {

    private KafkaTemplate<String, OrderMessage> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ListenableFuture<SendResult<String, OrderMessage>> handleMessage(OrderMessage orderMessage) {
        log.info(">>> Publishing message to t.commodity.order with Order # {} ", orderMessage.getOrderNumber());
/*
        var orderRecord = new ProducerRecord<>("t.commodity.order",
                orderMessage.getOrderGeneratedId(), orderMessage);*/
        var orderRecord = buildProducerRecord(orderMessage);
        var listenableFuture = this.kafkaTemplate.send(orderRecord);
        listenableFuture.addCallback(new KafkaSendCallback<>() {

            @Override
            public void onSuccess(SendResult<String, OrderMessage> result) {
                handleSuccess(orderMessage.getOrderGeneratedId(), orderMessage, result);
            }

            @Override
            public void onFailure(KafkaProducerException ex) {
                log.info("#####  On Failure #######"+ex.getMessage());
                ProducerRecord<String, OrderMessage> failed = ex.getFailedProducerRecord();
                handleFailure(failed, ex);
                //handleFailure(null, ex);
            }

        });
        return listenableFuture;

    }

    private ProducerRecord<String, OrderMessage> buildProducerRecord(OrderMessage orderMessage) {
        //Added Feature: Surprise Bonus for employees base on the order Location
        var contains = List.of("A", "B", "C", "T").contains(orderMessage.getLocation().substring(0, 1));
        List<Header> supriseBonus;
        if (contains) {
            supriseBonus = List.of(new RecordHeader("supriseBonus", "20".getBytes()));
        } else {
            supriseBonus = List.of(new RecordHeader("supriseBonus", "15".getBytes()));
        }
        return new ProducerRecord("t.commodity.order", null,
                orderMessage.getOrderGeneratedId(), orderMessage, supriseBonus);
    }

    private void handleFailure(ProducerRecord<String, OrderMessage> failed, KafkaProducerException ex) {
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

    private void handleSuccess(String name, OrderMessage orderMessage, SendResult<String, OrderMessage> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}",
                name, orderMessage, result.getRecordMetadata().partition());
    }

}
