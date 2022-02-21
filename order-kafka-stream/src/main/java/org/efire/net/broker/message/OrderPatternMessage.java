package org.efire.net.broker.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.apache.kafka.streams.kstream.Predicate;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
public class OrderPatternMessage {
    private String itemName;
    private String orderNumber;
    private String location;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime orderDateTime;
    private Double totalAmount;
    private OrderMessage orderMessage;

    public static OrderPatternMessage build(OrderMessage orderMessage) {
        return builder().itemName(orderMessage.getItem())
                .orderNumber(orderMessage.getOrderNumber())
                .location(orderMessage.getLocation())
                .orderDateTime(orderMessage.getOrderDateTime())
                .totalAmount(orderMessage.getPrice() * orderMessage.getQuantity())
                .build();
    }

    public static Predicate<? super String, ? super OrderPatternMessage> isPlastic() {
        return (k,v) -> v.getItemName().toLowerCase().contains("plastic");
    }
}
