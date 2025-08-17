package com.bookstore.entity;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;


    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name can’t be more than 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    @Pattern(regexp = "^[A-Za-z .]+$", message = "Name must only contain letters and spaces")
    private String name;

    // ===== Constructors ===== //
    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    // ===== Getters & Setters ===== //

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
