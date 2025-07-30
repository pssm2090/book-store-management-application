package com.bookstore.controller;

import com.bookstore.entity.Category;
import com.bookstore.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
    private CategoryService categoryService;

    
    

    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category saved = categoryService.addCategory(category);
        return ResponseEntity.ok(saved);
    }

    
    
    
    
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
    	List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/id/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        Optional<Category> category = categoryService.getCategoryById(categoryId);
        return category.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{categoryName}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String categoryName) {
        Optional<Category> category = categoryService.getCategoryByName(categoryName);
        return category.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    
    
    
    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category updatedCategory) {
    	Optional<Category> existingCategory = categoryService.getCategoryById(categoryId);
    	
    	if(existingCategory.isPresent()) {
    		Category savedCategory = categoryService.updateCategory(categoryId, updatedCategory.getName());
    		return ResponseEntity.ok(savedCategory);
    	} else {
    		return ResponseEntity.notFound().build();
    	}
    }

 
    
    
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
    		categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok("Category deleted successfully");
    }
}
