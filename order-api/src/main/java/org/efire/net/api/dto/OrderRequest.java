package org.efire.net.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ToString
public class OrderRequest {

    private String orderNumber;
    private String location;
    private String creditCardNumber;
    private List<OrderItemRequest> items;
}
