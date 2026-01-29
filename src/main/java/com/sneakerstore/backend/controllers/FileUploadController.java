package com.sneakerstore.backend.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload") // Đã thêm dấu "/" cho chuẩn
@CrossOrigin("*") // Cho phép React gọi API
@RequiredArgsConstructor // Tự động Inject Cloudinary

public class FileUploadController {

    private final Cloudinary cloudinary;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Kiểm tra file rỗng
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng chọn file!");
            }

        
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "sneaker_products", 
                "public_id", UUID.randomUUID().toString(),
                "resource_type", "auto"
            ));

            // 3. Lấy đường dẫn ảnh từ kết quả trả về
            // 'secure_url' là đường dẫn https (an toàn)
            String fileUrl = (String) uploadResult.get("secure_url");

            // 4. Trả về cho Frontend (Cấu trúc JSON y hệt cũ để Frontend không phải sửa gì)
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            
            // Lấy thêm tên file trên cloud nếu cần lưu database (public_id)
            response.put("publicId", (String) uploadResult.get("public_id")); 

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi upload file lên Cloud: " + e.getMessage());
        }
    }
}