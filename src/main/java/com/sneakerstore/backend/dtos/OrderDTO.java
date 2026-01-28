package com.sneakerstore.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @JsonProperty("customer_name")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;
    private String note;

    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("user_id")
    private Long userId; // Có thể null

    @JsonProperty("order_details")
    private List<OrderDetailDTO> orderDetails;

    // Class con cho chi tiết
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDetailDTO {
        @JsonProperty("product_id")
        private Long productId;
        
        @JsonProperty("variant_id") // Quan trọng
        private Long variantId;

        @JsonProperty("quantity")
        private int quantity;

        private Float price;
    }
}