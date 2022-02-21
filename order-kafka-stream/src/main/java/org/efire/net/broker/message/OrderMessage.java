package org.efire.net.broker.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import java.time.LocalDateTime;
import java.util.Base64;

@NoArgsConstructor
@Getter @Setter
@ToString
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class OrderMessage {

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

    public static OrderMessage maskedCreditCardNumber(OrderMessage orderMessage) {
        orderMessage.setCreditCardNumber(orderMessage.maskedCreditCardNumber());
        return orderMessage;
    }

    //Generate a Base 64 Storage Key
    public static KeyValueMapper<String, OrderMessage, String> generateStorageKey() {
        return (k, v) -> Base64.getEncoder().encodeToString(v.getOrderNumber().getBytes());
    }

    private String maskedCreditCardNumber() {
        var masked = this.creditCardNumber.replaceFirst("\\d{12}",
                StringUtils.repeat("*", 12));
        return masked;
    }

}
