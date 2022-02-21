package org.efire.net.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class OrderItemRequest {
    private String itemName;
    private Double price;
    private Integer quantity;
}
