package org.efire.net.api.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode
public class DiscountRequest {
    private String discountCode;
    private Integer percentValue;
}
