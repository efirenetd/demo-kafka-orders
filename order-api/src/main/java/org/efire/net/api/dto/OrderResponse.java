package org.efire.net.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class OrderResponse {
    private String status;
    private String orderNumber;
}
