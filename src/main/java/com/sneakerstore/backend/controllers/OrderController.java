package com.sneakerstore.backend.controllers;

import com.sneakerstore.backend.dtos.OrderDTO;
import com.sneakerstore.backend.models.Order;
import com.sneakerstore.backend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/orders") // Hoặc để cứng là "/api/orders" nếu chưa cấu hình prefix
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            // Gọi service để tạo đơn hàng
            Order newOrder = orderService.createOrder(orderDTO);
            
            // Trả về kết quả (có thể trả về DTO hoặc Entity tùy nhu cầu, ở đây trả Entity cho nhanh)
            return ResponseEntity.ok(newOrder);
        } catch (Exception e) {
            // Nếu có lỗi (ví dụ: Hết hàng, sai dữ liệu) -> Trả về lỗi 400 kèm thông báo
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable("user_id") Long userId) {
        try {
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/admin/get-all")
    public ResponseEntity<?> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Cập nhật trạng thái đơn (Admin)
    @PutMapping("/admin/update-status/{id}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id, 
            @RequestParam String status) { // Nhận status qua param (?status=SHIPPING)
        try {
            orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok("Cập nhật trạng thái thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}