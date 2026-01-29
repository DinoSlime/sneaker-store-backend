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
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Kích hoạt CORS với cấu hình bên dưới
            .authorizeHttpRequests(auth -> auth

                // 1. Đăng ký, Đăng nhập
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                // 2. Xem danh sách và chi tiết Sản phẩm, Danh mục (GET only)
                .requestMatchers(HttpMethod.GET, "/api/categories/**", "/api/products/**").permitAll()

                // 3. Xem ảnh sản phẩm (Rất quan trọng để hiển thị frontend)
                .requestMatchers(HttpMethod.GET, "/api/products/images/**", "/api/images/**").permitAll()
                
                // 4. MỞ QUYỀN XEM ẢNH TRONG THƯ MỤC UPLOAD
                .requestMatchers("/images/**").permitAll()

                // 5. CẤU HÌNH API UPLOAD
                .requestMatchers("/api/upload/**").permitAll() // Test xong nhớ đổi lại role nếu cần

                // 6. Quản lý Category (ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")

                // 7. Quản lý Product (ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                // 8. Quản lý Đơn hàng (Admin)
                .requestMatchers("/api/orders/get-all-orders").hasRole("ADMIN")
                .requestMatchers("/api/orders/update-status/**").hasRole("ADMIN")

                // 9. Đặt hàng & User
                .requestMatchers(HttpMethod.POST, "/api/orders/**").authenticated()
                .requestMatchers("/api/payment/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()
                .requestMatchers("/api/users/details").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/users/details/**").authenticated()

                // Các request khác
                .anyRequest().authenticated())
            
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // --- SỬA QUAN TRỌNG TẠI ĐÂY ---
        // Thay vì setAllowedOrigins cố định, ta dùng setAllowedOriginPatterns("*")
        // Điều này cho phép Vercel, Render, Localhost hay bất cứ đâu đều gọi được API
        configuration.setAllowedOriginPatterns(List.of("*")); 
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); 
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        configuration.setAllowCredentials(true); // Cho phép gửi cookie/auth header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}