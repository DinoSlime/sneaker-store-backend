package com.sneakerstore.backend.services.impl;

import com.sneakerstore.backend.components.JwtTokenUtil;
import com.sneakerstore.backend.models.Role;
import com.sneakerstore.backend.models.User;
import com.sneakerstore.backend.repositories.RoleRepository;
import com.sneakerstore.backend.repositories.UserRepository;
import com.sneakerstore.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException; // Thêm import này
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public User createUser(User user) {
        // 1. Kiểm tra trùng username
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DataIntegrityViolationException("Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!");
        }

        // 2. Kiểm tra trùng số điện thoại (Bắt buộc riêng biệt)
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Số điện thoại này đã được đăng ký!");
        }

        // 3. Gán quyền (Role)
        // ⚠️ LƯU Ý: Hãy chắc chắn Role ID 1 trong Database là "USER".
        // Nếu ID 1 là Admin thì toang! Tốt nhất nên check DB trước.
        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền mặc định (Role ID = 1)"));
        user.setRole(role);

        // 4. Mặc định tài khoản mới tạo sẽ hoạt động luôn
        user.setActive(true);

        // 5. Mã hóa mật khẩu
        if (user.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }

        return userRepository.save(user);
    }

    @Override
    public String login(String username, String password) throws Exception {
        // 1. Tìm user theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Sai tài khoản hoặc mật khẩu"));

        // 2. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        }

        // 3. 👇 QUAN TRỌNG: Kiểm tra xem tài khoản có bị khóa không?
        if (!user.isActive()) {
            throw new DisabledException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin.");
        }

        // 4. Sinh Token
        return jwtTokenUtil.generateToken(user);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + id));
    }

    @Override
    public User getUserDetails(String username) throws Exception {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));
    }
}