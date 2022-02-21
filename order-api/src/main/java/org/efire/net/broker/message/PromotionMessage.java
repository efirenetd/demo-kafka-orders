package org.efire.net.broker.message;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class PromotionMessage {
    private String promotionCode;
}
