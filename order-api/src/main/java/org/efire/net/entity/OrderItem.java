package org.efire.net.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItem {

    @Id
    @GeneratedValue
    private Integer itemId;

    @Column(nullable = false, length = 200)
    private String itemName;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    @JoinColumn(name = "order_id")
    @ManyToOne
    private OrderTaker orderTaker;
}
