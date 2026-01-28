package com.sneakerstore.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 👇 QUAN TRỌNG: Để biết khách mua Size/Màu nào để trừ kho
    @ManyToOne 
    @JoinColumn(name = "variant_id")
    private ProductVariant variant; 

    @Column(name = "price", nullable = false)
    private Float price; // Giá tại thời điểm mua

    @Column(name = "number_of_products", nullable = false)
    private int numberOfProducts; // Số lượng

    @Column(name = "total_money", nullable = false)
    private Float totalMoney; // = price * numberOfProducts
}