package com.sneakerstore.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sneakerstore.backend.dtos.PaymentResponseDTO;

@RestController
@RequestMapping("api/payment")
@CrossOrigin("*")
public class PaymentController {

    @PostMapping("/vietqr")
    public ResponseEntity<?> getVietQR(@RequestBody OrderResponseController order) {
        String bankId = "MB"; // Thay bằng mã ngân hàng của bạn (VCB, ICB, v.v.)
        String accountNo = "56468877180054"; // Số tài khoản thực tế của bạn
        String accountName = "THANH NINH BINH"; // Tên của bạn viết hoa không dấu

        String description = "THANHTOAN " + order.getId();

        // Cấu trúc link từ vietqr.io (mì ăn liền và cực ổn định)
        String qrUrl = String.format(
                "https://img.vietqr.io/image/%s-%s-compact.png?amount=%d&addInfo=%s&accountName=%s",
                bankId, accountNo, order.getTotalPrice(), description, accountName);

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setQrCodeUrl(qrUrl);
        response.setOrderId(String.valueOf(order.getId()));
        response.setTotalAmount(order.getTotalPrice());
        response.setDescription(description);

        return ResponseEntity.ok(response);
    }
}
