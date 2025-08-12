package com.bookstore.service;

import com.bookstore.entity.Category;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.exception.CategoryNotFoundException;
import com.bookstore.exception.DuplicateCategoryNameException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryService {
	
	@Autowired
    private CategoryRepository categoryRepository;

	
	public Category addCategory(Category category) {
        categoryRepository.findByNameIgnoreCase(category.getName()).ifPresent(existing -> {
            throw new DuplicateCategoryNameException("Category with name '" + category.getName() + "' already exists.");
        });
        return categoryRepository.save(category);
    }

	
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
    }
    
    
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with name: " + categoryName));
    }
	
    
    public Category updateCategory(Long categoryId, String newName) {
        Category category = getCategoryById(categoryId);

        categoryRepository.findByNameIgnoreCase(newName).ifPresent(existing -> {
            if (!existing.getCategoryId().equals(categoryId)) {
                throw new DuplicateCategoryNameException("Another category with name '" + newName + "' already exists.");
            }
        });

        category.setName(newName);
        return categoryRepository.save(category);
    }

    
    public void deleteCategory(Long categoryId) {
    	if (!categoryRepository.existsById(categoryId)) {
    	    throw new CategoryNotFoundException("Category not found with id: " + categoryId);
    	}
        categoryRepository.deleteById(categoryId);
    }

}
