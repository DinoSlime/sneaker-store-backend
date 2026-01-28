package com.sneakerstore.backend.services;

import com.sneakerstore.backend.models.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long id, Category categoryDetails);
    void deleteCategory(long id);
}