package com.sneakerstore.backend.services.impl;

import com.sneakerstore.backend.dtos.ProductDTO;
import com.sneakerstore.backend.dtos.ProductVariantDTO;
import com.sneakerstore.backend.models.Category;
import com.sneakerstore.backend.models.Product;
import com.sneakerstore.backend.models.ProductVariant;
import com.sneakerstore.backend.repositories.CategoryRepository;
import com.sneakerstore.backend.repositories.ProductRepository;
import com.sneakerstore.backend.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional 
    public Product createProduct(ProductDTO productDTO) throws Exception {
        
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new Exception("Không tìm thấy Category với ID: " + productDTO.getCategoryId()));

      
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();

       
        if (productDTO.getVariants() != null && !productDTO.getVariants().isEmpty()) {
            List<ProductVariant> variants = new ArrayList<>();
            
            for (ProductVariantDTO variantDTO : productDTO.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(newProduct) 
                        .size(variantDTO.getSize())
                        .color(variantDTO.getColor())
                        .imageUrl(variantDTO.getImageUrl())
                        .stock(variantDTO.getStock())
                        .build();
                variants.add(variant);
            }
           
            newProduct.setVariants(variants);
        }

      
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long id) throws Exception {
        return productRepository.findById(id)
                .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm id: " + id));
    }

    @Override
    public Page<Product> getAllProducts(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest);
    }

    @Override
    public Page<Product> searchProducts(String keyword, Long categoryId, Float minPrice, Float maxPrice, PageRequest pageRequest) {
        return productRepository.searchProducts(keyword, categoryId, minPrice, maxPrice, pageRequest);
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        if(productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));

        // 1. Cập nhật thông tin cơ bản
        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setThumbnail(productDTO.getThumbnail());
        existingProduct.setDescription(productDTO.getDescription());

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            existingProduct.setCategory(category);
        }

        // 2. Cập nhật Variants (Size/Màu/Stock)
        if (productDTO.getVariants() != null) {
            // Xóa hết size cũ đi (Nhờ orphanRemoval=true nên nó sẽ xóa trong DB luôn)
            existingProduct.getVariants().clear();
            
            // Thêm lại size mới từ request gửi lên
            for (ProductVariantDTO variantDTO : productDTO.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(existingProduct) // Gắn lại vào sản phẩm cha
                        .size(variantDTO.getSize())
                        .color(variantDTO.getColor())
                        .imageUrl(variantDTO.getImageUrl())
                        .stock(variantDTO.getStock()) // Cập nhật stock mới (ví dụ = 0)
                        .build();
                existingProduct.getVariants().add(variant);
            }
        }

        return productRepository.save(existingProduct);
    }
}