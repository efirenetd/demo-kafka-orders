package org.efire.net.broker.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
public class OrderRewardMessage {
    private Integer orderId;
    private String orderNumber;
    private String location;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime orderDateTime;
    private String item;
    private Integer quantity;
    private Double price;

    public static OrderRewardMessage mapToOrderRewardMessage(OrderMessage orderMessage) {
        return builder()
                .item(orderMessage.getItem())
                .orderId(orderMessage.getOrderId())
                .orderNumber(orderMessage.getOrderNumber())
                .location(orderMessage.getLocation())
                .orderDateTime(orderMessage.getOrderDateTime())
                .price(orderMessage.getPrice())
                .quantity(orderMessage.getQuantity())
                .build();
    }

    public static Predicate<String, OrderMessage> isLargeQuantity() {
        return (k, v) -> v.getQuantity() > 200;
    }

    public static Predicate<? super String,? super OrderMessage> isCheap() {
        return (k, v) -> v.getPrice() < 100;
    }

    public static KeyValueMapper<String, OrderMessage, KeyValue<String, OrderRewardMessage>> mapToOrderRewardChangeKey() {
        return (k, v) -> KeyValue.pair(v.getLocation(), mapToOrderRewardMessage(v));
    }
}

