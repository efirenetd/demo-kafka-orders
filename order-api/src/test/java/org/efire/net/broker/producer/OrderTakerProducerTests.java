package org.efire.net.broker.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.efire.net.broker.message.OrderMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderTakerProducerTests {

    public static final String T_COMMODITY_ORDER = "t.commodity.order";
    @Mock
    private KafkaTemplate<String, OrderMessage> kafkaTemplate;

    @InjectMocks
    private OrderProducer orderProducer;
    private OrderMessage orderMessage;
    private ProducerRecord<String, OrderMessage> orderProducerRecord;

    @BeforeEach
    void setUp() {
        orderMessage = OrderMessage.builder()
                .orderId(1)
                .orderNumber("2")
                .location("Tokyo")
                .creditCardNumber("12345xxxx")
                .item("itemx")
                .quantity(1)
                .price(199.99)
                .orderDateTime(LocalDateTime.now())
                .build();

        orderProducerRecord = new ProducerRecord<>(T_COMMODITY_ORDER,
                null,
                orderMessage.getOrderNumber(),
                orderMessage);

    }

    @Test
    void shouldPublishOrderMessageExpectSuccess() throws ExecutionException, InterruptedException {
        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition(T_COMMODITY_ORDER, 1),
                0, 1, System.currentTimeMillis(),
                0L, 0,0);

        var orderMessageSendResult = new SendResult<>(orderProducerRecord, recordMetadata);
        var listenableFuture = new SettableListenableFuture();
        listenableFuture.set(orderMessageSendResult);

        when(kafkaTemplate.send(orderProducerRecord)).thenReturn(listenableFuture);
        var resultListenableFuture = orderProducer.handleMessage(orderMessage);

        assertThat(resultListenableFuture.isDone()).isTrue();
        assertThat(resultListenableFuture.get().getRecordMetadata().topic()).isEqualTo(T_COMMODITY_ORDER);
    }

    @Test
    public void shouldPublishOrderMessageExpectFailure() throws InterruptedException {
        var listenableFuture = new SettableListenableFuture();
        listenableFuture.setException(new KafkaProducerException(orderProducerRecord, "Exception thrown to fail", new IllegalArgumentException("aaa")));

        when(kafkaTemplate.send(orderProducerRecord)).thenReturn(listenableFuture);

        //TODO Fixed the failure test
        assertThrows(ExecutionException.class, () -> {
            orderProducer.handleMessage(orderMessage).get(10, TimeUnit.SECONDS);
        });
    }
}
