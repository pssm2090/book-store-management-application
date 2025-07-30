package com.bookstore.service;

import com.bookstore.entity.Category;
import com.bookstore.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
	
	@Autowired
    private CategoryRepository categoryRepository;

	
	public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

	
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }
    
    
    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryRepository.findByNameIgnoreCase(categoryName);
    }
	
	
    public Category updateCategory(Long categoryId, String newName) {
        return categoryRepository.findById(categoryId).map(existing -> {
            existing.setName(newName);
            return categoryRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }

    
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

}
