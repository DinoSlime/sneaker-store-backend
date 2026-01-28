package com.sneakerstore.backend.services.impl;

import com.sneakerstore.backend.dtos.OrderDTO;
import com.sneakerstore.backend.models.*;
import com.sneakerstore.backend.repositories.*;
import com.sneakerstore.backend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor // Tự động Inject Repository (thay cho @Autowired)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional // Nếu có lỗi ở bất kỳ bước nào, rollback lại hết
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        // 1. Tìm User nếu có (để gắn vào đơn hàng)
        User user = null;
        if (orderDTO.getUserId() != null) {
            user = userRepository.findById(orderDTO.getUserId())
                    .orElse(null); // Nếu ko tìm thấy thì user = null (vẫn cho mua)
        }

        // 2. Tạo và Lưu Order (Bảng cha)
        Order order = Order.builder()
                .fullName(orderDTO.getFullName())
                .phoneNumber(orderDTO.getPhoneNumber())
                .address(orderDTO.getAddress())
                .note(orderDTO.getNote())
                .totalMoney(orderDTO.getTotalMoney())
                .paymentMethod(orderDTO.getPaymentMethod())
                .status("PENDING")
                .orderDate(LocalDateTime.now())
                .user(user)
                .build();

        // Lưu order trước để lấy ID
        Order savedOrder = orderRepository.save(order);

        // 3. Xử lý từng món hàng trong chi tiết (Bảng con)
        List<OrderDetail> details = new ArrayList<>();

        for (OrderDTO.OrderDetailDTO itemDTO : orderDTO.getOrderDetails()) {
            // Lấy thông tin sản phẩm
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm ID: " + itemDTO.getProductId()));

            // Lấy thông tin biến thể (Size/Màu)
            ProductVariant variant = variantRepository.findById(itemDTO.getVariantId())
                    .orElseThrow(() -> new Exception("Không tìm thấy biến thể ID: " + itemDTO.getVariantId()));

            // CHECK TỒN KHO & TRỪ KHO
            if (variant.getStock() < itemDTO.getQuantity()) {
                throw new Exception("Sản phẩm " + product.getName() + " (Size: " + variant.getSize() + ") không đủ hàng!");
            }
            // Trừ số lượng tồn kho
            variant.setStock(variant.getStock() - itemDTO.getQuantity());
            variantRepository.save(variant);

            // Tạo OrderDetail
            OrderDetail detail = OrderDetail.builder()
                    .order(savedOrder)
                    .product(product)
                    .variant(variant)
                    .price(itemDTO.getPrice())
                    .numberOfProducts(itemDTO.getQuantity())
                    .totalMoney(itemDTO.getPrice() * itemDTO.getQuantity())
                    .build();

            details.add(detail);
        }

        // Lưu danh sách chi tiết đơn hàng
        orderDetailRepository.saveAll(details);

        return savedOrder;
    }
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
       return orderRepository.findByUserId(userId);
    }

    @Override
    public Order getOrderById(Long orderId) throws Exception {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Không tìm thấy đơn hàng ID: " + orderId));
    }
    @Override
    public List<Order> getAllOrders() {
        // Lấy tất cả đơn hàng, sắp xếp mới nhất lên đầu (ID giảm dần)
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));
        
        order.setStatus(status);
        orderRepository.save(order);
    }
}