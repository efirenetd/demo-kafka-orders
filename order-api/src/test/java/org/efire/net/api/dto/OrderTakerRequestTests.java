package org.efire.net.api.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@JsonTest

public class OrderTakerRequestTests {

    private OrderRequest orderRequest;

    @Autowired
    private JacksonTester<OrderRequest> json;

    @BeforeEach
    void setUp() {
        var coffeeOrderItem = new OrderItemRequest();
        coffeeOrderItem.setItemName("Cafe Americano");
        coffeeOrderItem.setQuantity(1);
        coffeeOrderItem.setPrice(5.50);

        var croissantItemRequest = new OrderItemRequest();
        croissantItemRequest.setItemName("Ham and cheese Croissant");
        croissantItemRequest.setQuantity(1);
        croissantItemRequest.setPrice(3.24);

        var orderRequest = new OrderRequest();
        orderRequest.setLocation("Trinoma");
        orderRequest.setOrderNumber("0001");
        orderRequest.setItems(Arrays.asList(coffeeOrderItem, croissantItemRequest));

        this.orderRequest = orderRequest;
    }

    //@Test
    void shouldDeserializeOrderRequest() throws IOException, URISyntaxException {
        Path jsonPath = Paths.get(new ClassPathResource("json/testOrderRequest.json").getURL().toURI());
        String jsonString = Files.readAllLines(jsonPath).stream().collect(Collectors.joining());
        System.out.println(jsonString);

        assertThat(this.json.parse(jsonString)).isEqualTo(orderRequest);
    }
}
