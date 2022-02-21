package org.efire.net.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.efire.net.api.dto.OrderResponse;
import org.efire.net.broker.producer.OrderProducer;
import org.efire.net.entity.OrderTaker;
import org.efire.net.entity.OrderItem;
import org.efire.net.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(OrderController.class)
public class OrderTakerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;
    @MockBean
    private OrderProducer orderProducer;

    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
    }

    @Test
    void shouldAbleToCreateOrder() throws Exception {
        var expectedJson = "{\"status\":\"Success\",\"orderNumber\":\"001\"}";
        ObjectMapper mapper = new ObjectMapper();
        Path jsonPath = Paths.get(new ClassPathResource("json/testOrderRequest.json").getURL().toURI());
        String jsonString = Files.readAllLines(jsonPath).stream().collect(Collectors.joining());
        List<OrderItem> items = new ArrayList<>();
        var orderItem = OrderItem.builder()
                .itemId(1)
                .itemName("test item")
                .price(100.00)
                .quantity(1)
                .build();
        items.add(orderItem);
        var order = OrderTaker.builder()
                .orderNumber("001")
                .location("TestLocation")
                .orderItems(items)
                .build();

        given(orderService.saveOrder(any())).willReturn(order);
        var mvcResult = mockMvc.perform(post("/v1/api/orders")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        var actualOrderResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), OrderResponse.class);
        var expectedOrderResponse = mapper.readValue(expectedJson, OrderResponse.class);
        assertThat(actualOrderResponse.getStatus()).isEqualTo(expectedOrderResponse.getStatus());
        assertThat(actualOrderResponse.getOrderNumber()).isEqualTo(expectedOrderResponse.getOrderNumber());
    }
}
