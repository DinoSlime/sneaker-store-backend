package com.sneakerstore.backend.services.impl;

import com.sneakerstore.backend.models.Category;
import com.sneakerstore.backend.repositories.CategoryRepository;
import com.sneakerstore.backend.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Category với ID: " + id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(long id, Category categoryDetails) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDetails.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Category để xóa với ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}