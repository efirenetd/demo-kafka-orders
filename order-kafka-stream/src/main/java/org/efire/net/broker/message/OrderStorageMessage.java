package org.efire.net.broker.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
public class OrderStorageMessage {
    private Integer orderId;
    private String orderNumber;
    private String location;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime orderDateTime;
    private String creditCardNumber;
    private String item;
    private Integer quantity;
    private Double price;

    public static OrderStorageMessage build(OrderMessage om) {
        return builder()
                .orderId(om.getOrderId())
                .orderNumber(om.getOrderNumber())
                .location(om.getLocation())
                .item(om.getItem())
                .orderDateTime(om.getOrderDateTime())
                .creditCardNumber(om.getCreditCardNumber())
                .quantity(om.getQuantity())
                .price(om.getPrice())
                .build();
    }
}
