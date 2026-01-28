package com.sneakerstore.backend.repositories;

import com.sneakerstore.backend.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
