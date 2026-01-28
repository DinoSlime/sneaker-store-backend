package com.sneakerstore.backend.components;

import com.sneakerstore.backend.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private final String SECRET_KEY = "GoOneStepFurtherToInnovation_SneakerStore_SecretKey_PleaseDoNotReveal_MoreLengthIsBetter";
    private final long EXPIRATION_TIME = 2592000 * 1000L;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        
        // 👇 1. Đưa SĐT vào làm thông tin phụ (lưu trong DB)
        claims.put("phoneNumber", user.getPhoneNumber());
        
        // 👇 2. QUAN TRỌNG: Đưa Role vào Token để Admin thực hiện được DELETE/PUT (Sửa lỗi 403)
        if (user.getRole() != null) {
            claims.put("role", user.getRole().getName());
        }

        return Jwts.builder()
                .setClaims(claims)
                // 👇 3. Đổi Subject từ PhoneNumber sang Username để khớp với Security
                .setSubject(user.getUsername()) 
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Đổi tên hàm từ extractPhoneNumber thành extractUsername cho đúng bản chất
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        // 👇 So sánh Username lấy từ Token với Username trong hệ thống
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // --- CÁC HÀM CÒN LẠI GIỮ NGUYÊN ---
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}