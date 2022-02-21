package org.efire.net.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_taker")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTaker {

    @Id
    @GeneratedValue
    private Integer orderId;

    @Column(nullable = false, length = 20)
    private String orderNumber;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @Column(nullable = false)
    private String creditCardNumber;

    @OneToMany(mappedBy = "orderTaker")
    private List<OrderItem> orderItems;

}
