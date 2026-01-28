package com.sneakerstore.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    
    private String name;
    
    private Float price;
    
    private String thumbnail;
    
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("variants")
    private List<ProductVariantDTO> variants;
}