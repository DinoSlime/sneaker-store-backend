package com.sneakerstore.backend.repositories;

import com.sneakerstore.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Sử dụng câu lệnh này để tìm đơn hàng dựa trên ID của User
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.id DESC")
    List<Order> findByUserId(@Param("userId") Long userId);
}