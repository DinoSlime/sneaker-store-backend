package com.sneakerstore.backend.services;

import com.sneakerstore.backend.dtos.OrderDTO;
import com.sneakerstore.backend.models.Order;
import java.util.List;
public interface OrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    List<Order> getOrdersByUserId(Long userId);
    Order getOrderById(Long orderId) throws Exception;
    List<Order> getAllOrders();
    void updateOrderStatus(Long orderId, String status);
}