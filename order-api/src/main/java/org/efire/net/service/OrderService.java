package org.efire.net.service;

import org.apache.commons.lang3.RandomUtils;
import org.efire.net.api.dto.OrderRequest;
import org.efire.net.entity.OrderTaker;
import org.efire.net.entity.OrderItem;
import org.efire.net.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderTaker saveOrder(OrderRequest orderRequest) {
        OrderTaker orderTaker = convertToOrder(orderRequest);
        return orderRepository.save(orderTaker);
    }

    private OrderTaker convertToOrder(OrderRequest orderRequest) {
        List<OrderItem> items = new ArrayList<>();
        orderRequest.getItems().stream().forEach(orderItemRequest -> {
            var orderItem = OrderItem.builder()
                    .itemId(RandomUtils.nextInt(1, 9999999))
                    .itemName(orderItemRequest.getItemName())
                    .price(orderItemRequest.getPrice())
                    .quantity(orderItemRequest.getQuantity())
                    .build();
            items.add(orderItem);
        });
        var order = OrderTaker.builder()
                .orderNumber(String.valueOf(RandomUtils.nextInt(1, 999999)))
                .location(orderRequest.getLocation())
                .creditCardNumber(orderRequest.getCreditCardNumber())
                .orderDateTime(LocalDateTime.now())
                .orderItems(items)
                .build();
        return order;
    }
}
