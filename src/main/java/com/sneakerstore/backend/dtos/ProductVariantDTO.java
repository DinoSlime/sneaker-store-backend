package com.sneakerstore.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data // Lombok tự tạo getter/setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {
    
    
    
    @JsonProperty("size")
    private Integer size;

    @JsonProperty("color")
    private String color;

    @JsonProperty("stock")
    private Integer stock;

    @JsonProperty("image_url")
    private String imageUrl;
}