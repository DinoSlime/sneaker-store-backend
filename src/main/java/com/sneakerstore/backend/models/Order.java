package com.sneakerstore.backend.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime; // Dùng cái này thay cho java.util.Date
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Thêm Builder cho dễ new đối tượng
public class Order extends BaseEntity {

    @Column(name = "fullname", length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20) 
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 255) // Tăng lên 255
    private String address;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "order_date")
    private LocalDateTime orderDate; // Sửa thành LocalDateTime

    @Column(name = "status")
    private String status; // PENDING, SHIPPING, DELIVERED, CANCELLED

    @Column(name = "total_money")
    private Float totalMoney;
    
    @Column(name = "payment_method")
    private String paymentMethod; // COD, BANK

    @ManyToOne
    @JoinColumn(name = "user_id") // Có thể null nếu khách vãng lai mua
    private User user;

    // 👇 QUAN TRỌNG: Mối quan hệ 1-N với chi tiết đơn hàng
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<OrderDetail> orderDetails;
}