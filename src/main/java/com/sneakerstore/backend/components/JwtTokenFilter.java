package com.sneakerstore.backend.components;

import com.sneakerstore.backend.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isBypassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String token = authHeader.substring(7);
            
            // 👇 1. Sửa thành extractUsername (Vì ta đã đổi Subject trong JwtTokenUtil)
            final String username = jwtTokenUtil.extractUsername(token); 
            System.out.println(">>> 1. USERNAME LẤY TỪ TOKEN: [" + username + "]");

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 👇 2. Tìm User bằng Username (Lúc này DB sẽ tìm đúng cột Username chữ)
                User userDetails = (User) userDetailsService.loadUserByUsername(username);
                
                System.out.println(">>> 2. TÌM THẤY USER TRONG DB: " + userDetails.getUsername());
                System.out.println(">>> 3. QUYỀN (ROLE) CỦA USER NÀY: " + userDetails.getAuthorities());

                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() 
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println(">>> 4. XÁC THỰC THÀNH CÔNG! QUYỀN " + userDetails.getRole().getName() + " ĐÃ ĐƯỢC CHẤP NHẬN.");
                } else {
                    System.out.println(">>> X. TOKEN KHÔNG HỢP LỆ VỚI USER NÀY!");
                }
            }
        } catch (Exception e) {
            System.err.println(">>> LỖI Ở FILTER: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of("/api/products", "GET"),
                Pair.of("/api/categories", "GET"),
                Pair.of("/api/users/register", "POST"),
                Pair.of("/api/users/login", "POST"),
                Pair.of("/api/images", "GET"));
        
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        for (Pair<String, String> bypassToken : bypassTokens) {
            if (requestPath.contains(bypassToken.getFirst()) && requestMethod.equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
}