package org.efire.net.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.efire.net.api.dto.OrderRequest;
import org.efire.net.api.dto.OrderResponse;
import org.efire.net.broker.message.OrderMessage;
import org.efire.net.broker.producer.OrderProducer;
import org.efire.net.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Api(tags = "Order API", value = "OrderAPI")
public class OrderController {

    private OrderService orderService;
    private OrderProducer orderProducer;

    public OrderController(OrderService orderService, OrderProducer orderProducer) {
        this.orderService = orderService;
        this.orderProducer = orderProducer;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a new order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        var orderResponse = new OrderResponse();
        var order = orderService.saveOrder(orderRequest);

        if (StringUtils.isNotBlank(order.getOrderNumber())) {
            orderResponse.setStatus("Success");

            orderResponse.setOrderNumber(order.getOrderNumber());
            //Publish to kafka
            order.getOrderItems().stream().forEach(orderItem -> {
                var orderMessage = OrderMessage.builder()
                        .creditCardNumber(order.getCreditCardNumber())
                        .location(order.getLocation())
                        .orderId(orderItem.getItemId())
                        .orderNumber(order.getOrderNumber())
                        .item(orderItem.getItemName())
                        .quantity(orderItem.getQuantity())
                        .price(orderItem.getPrice())
                        .orderDateTime(order.getOrderDateTime())
                        .build();
                orderProducer.handleMessage(orderMessage);
            });
        }

        return ResponseEntity.ok(orderResponse);
    }

}
