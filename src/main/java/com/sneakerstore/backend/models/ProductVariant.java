package com.sneakerstore.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "size")
    private Integer size; 

    @Column(name = "color")
    private String color; 

    @Column(name = "image_url")
    private String imageUrl; 

    @Column(name = "stock")
    private Integer stock; 

   
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference 
    private Product product;
}