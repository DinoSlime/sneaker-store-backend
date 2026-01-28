package com.sneakerstore.backend.dtos;

import lombok.Data; 

@Data 
public class PaymentResponseDTO {
    private String qrCodeUrl;
    private String orderId;
    private Long totalAmount;
    private String description;
}