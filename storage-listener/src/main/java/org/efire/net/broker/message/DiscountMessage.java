package org.efire.net.broker.message;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class DiscountMessage {
    private String discountCode;
    private Integer percentValue;
}
