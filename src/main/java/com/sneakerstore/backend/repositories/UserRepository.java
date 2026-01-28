package com.sneakerstore.backend.repositories;

import com.sneakerstore.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 1. Kiểm tra xem username đã tồn tại chưa (Dùng khi Đăng Ký)
    boolean existsByUsername(String username);

    // 2. Tìm User bằng username (Dùng khi Đăng Nhập)
    Optional<User> findByUsername(String username);

    // 3. Kiểm tra xem SĐT đã tồn tại chưa (Sửa dòng này lại cho đúng)
    boolean existsByPhoneNumber(String phoneNumber); 
}