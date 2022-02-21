package org.efire.net.broker.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@ToString
@AllArgsConstructor
@Builder
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

    @JsonIgnore
    public String getOrderGeneratedId() {
        return "SN-".concat(orderId.toString());
    }
}
