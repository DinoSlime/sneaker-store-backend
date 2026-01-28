package com.sneakerstore.backend.controllers;

import com.fasterxml.jackson.annotation.JsonProperty; // 👇 Import cái này
import lombok.Data;

@Data
public class OrderResponseController {
    
    // 👇 Thêm dòng này để map đúng trường ID từ React gửi lên
    @JsonProperty("id") 
    private Long id;

    // 👇 QUAN TRỌNG: Map trường 'total_money' (hoặc totalMoney) vào biến này
    @JsonProperty("total_money") 
    private Long totalPrice; 
    
    // Nếu API tạo đơn hàng của bạn trả về 'totalMoney' (viết liền), hãy thêm setter phụ:
    @JsonProperty("totalMoney")
    public void setTotalMoneyAlias(Long totalMoney) {
        this.totalPrice = totalMoney;
    }
}