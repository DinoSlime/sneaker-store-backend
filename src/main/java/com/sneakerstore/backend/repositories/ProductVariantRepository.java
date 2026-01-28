
package com.sneakerstore.backend.repositories;

import com.sneakerstore.backend.models.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
}
