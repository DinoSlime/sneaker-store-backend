package com.sneakerstore.backend.configurations;

import com.sneakerstore.backend.components.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        // Đăng ký, Đăng nhập
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                        // Xem danh sách và chi tiết Sản phẩm, Danh mục (GET only)
                        .requestMatchers(HttpMethod.GET, "/api/categories/**", "/api/products/**").permitAll()

                        // Xem ảnh sản phẩm (Rất quan trọng để hiển thị frontend)
                        .requestMatchers(HttpMethod.GET, "/api/products/images/**", "/api/images/**").permitAll()
                        
                        // 👇 MỞ QUYỀN XEM ẢNH TRONG THƯ MỤC UPLOAD
                        .requestMatchers("/images/**").permitAll()

                        // 👇 CẤU HÌNH API UPLOAD (QUAN TRỌNG)
                        // Nếu muốn ai cũng upload được (để test): .permitAll()
                        // Nếu chỉ Admin được upload: .hasRole("ADMIN")
                        .requestMatchers("/api/upload/**").permitAll()

                        // Quản lý Category (Thêm, Sửa, Xóa)
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")

                        // Quản lý Product (Thêm, Sửa, Xóa)
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // Quản lý Đơn hàng (Dành riêng cho Admin)
                        .requestMatchers("/api/orders/get-all-orders").hasRole("ADMIN") // Cụ thể hoá API admin
                        .requestMatchers("/api/orders/update-status/**").hasRole("ADMIN")

                        // Đặt hàng (POST)
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").authenticated()
                        .requestMatchers("/api/payment/**").authenticated()
                        
                        // Xem lịch sử đơn hàng, chi tiết đơn hàng (GET)
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()

                        // Xem/Sửa thông tin cá nhân (User)
                        .requestMatchers("/api/users/details").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/details/**").authenticated()

                        // Tất cả các request khác chưa khai báo -> Phải đăng nhập mới được vào
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép cả localhost:5173 (Vite) và 3000 (Create React App)
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        // Cho phép tất cả các method quan trọng, bao gồm OPTIONS (cho preflight check của CORS)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); 
        // Cho phép tất cả header để tránh lỗi thiếu header
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}