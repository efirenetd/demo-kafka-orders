package org.efire.net.broker.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter @Setter
@ToString
public class DiscountMessage {
    private String discountCode;
    private Integer percentValue;

    public Integer percentToDecimal() {
        return this.percentValue / 100;
    }
}
