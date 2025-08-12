package com.bookstore.controller;

import com.bookstore.entity.Category;
import com.bookstore.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
    private CategoryService categoryService;

	
	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@Valid @RequestBody Category category) {
        Category saved = categoryService.addCategory(category);
        return ResponseEntity.ok(saved);
    }

    
    
    @GetMapping("/get-all")
    public ResponseEntity<List<Category>> getAllCategories() {
    	List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/id/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/name/{categoryName}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String categoryName) {
        Category category = categoryService.getCategoryByName(categoryName);
        return ResponseEntity.ok(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody Category updatedCategory) {
    		Category savedCategory = categoryService.updateCategory(categoryId, updatedCategory.getName());
    		return ResponseEntity.ok(savedCategory);

    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
    		categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok("Category deleted successfully");
    }
}
