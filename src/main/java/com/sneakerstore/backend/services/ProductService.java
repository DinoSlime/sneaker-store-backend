package com.sneakerstore.backend.services;

import com.sneakerstore.backend.dtos.ProductDTO;
import com.sneakerstore.backend.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product updateProduct(Long id, ProductDTO productDTO) throws Exception;
    Product getProductById(long id) throws Exception;
    Page<Product> getAllProducts(PageRequest pageRequest);
    Page<Product> searchProducts(String keyword, Long categoryId, Float minPrice, Float maxPrice, PageRequest pageRequest);
    void deleteProduct(long id);
}