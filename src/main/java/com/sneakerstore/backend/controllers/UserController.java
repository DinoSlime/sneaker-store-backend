package com.sneakerstore.backend.controllers;

import com.sneakerstore.backend.dtos.UserLoginDTO;
import com.sneakerstore.backend.dtos.UserRegisterDTO;
import com.sneakerstore.backend.models.User;
import com.sneakerstore.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            // 1. Kiểm tra mật khẩu nhập lại có khớp không
            if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp!");
            }

            // 2. Map dữ liệu từ DTO sang Entity (Bổ sung đầy đủ các trường)
            User user = new User();
            user.setFullName(userRegisterDTO.getFullName());
            user.setUsername(userRegisterDTO.getUsername());
            user.setPassword(userRegisterDTO.getPassword());
            
            // QUAN TRỌNG: Phải map thêm các trường này
            user.setPhoneNumber(userRegisterDTO.getPhoneNumber()); // Bắt buộc (nullable = false)
            user.setAddress(userRegisterDTO.getAddress());
            user.setDateOfBirth(userRegisterDTO.getDateOfBirth());
            user.setFacebookAccountId(userRegisterDTO.getFacebookAccountId());
            user.setGoogleAccountId(userRegisterDTO.getGoogleAccountId());
            
            // 3. Gọi Service để tạo User
            User newUser = userService.createUser(user);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            String token = userService.login(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            User user = userService.getUserDetails(userLoginDTO.getUsername());
            
            // 👇 SỬA ĐOẠN NÀY: Tạo response thủ công, KHÔNG trả về nguyên đối tượng User
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            
            // Tạo object user gọn nhẹ để gửi về Frontend
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("fullName", user.getFullName());
            // Quan trọng: Chỉ lấy tên Role (ví dụ "ADMIN") chứ không lấy cả object Role
            userInfo.put("role", user.getRole().getName().toUpperCase()); 
            
            result.put("user", userInfo);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}