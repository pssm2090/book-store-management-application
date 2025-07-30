package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BookService {

	@Autowired
    private BookRepository bookRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	public Book addBook(Book book) {
		if(book.getCategory() != null && book.getCategory().getName() != null) {
			String categoryName = book.getCategory().getName();
			
			Category category = categoryRepository.findByNameIgnoreCase(categoryName)
					.orElseGet(() -> categoryRepository.save(new Category(categoryName)));
			book.setCategory(category);
		} else {
			throw new IllegalArgumentException("Category name must be provided.");
		}
        return bookRepository.save(book);
    }
	
	public void bulkUpload(MultipartFile file) {
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
	         CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

	        List<Book> books = new ArrayList<>();
	        
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


	        for (CSVRecord record : csvParser) {
	        	try {
	        		Book book = new Book();
	        		book.setTitle(record.get("title").trim());
	        		book.setAuthor(record.get("author").trim());
	        		book.setDescription(record.get("description").trim());
	        		book.setPrice(new BigDecimal(record.get("price").trim()));
	        		book.setIsbn(record.get("isbn").trim());
	        		book.setPublishedDate(LocalDate.parse(record.get("publishedDate").trim(), formatter));
	        		book.setStockQuantity(Integer.parseInt(record.get("stockQuantity").trim()));

	        		String categoryName = record.get("categoryName").trim();
	        		Category category = categoryRepository.findByNameIgnoreCase(categoryName)
	                                 .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
	        		book.setCategory(category);


//	            	book.setCoverImageUrl(record.get("coverImageUrl"));
	        		books.add(book);
	        	} catch (Exception ex) {
	                System.err.println("Error parsing row: " + record + " -> " + ex.getMessage());
	            }
	        }

	        bookRepository.saveAll(books);
	        System.out.println("Successfully saved " + books.size() + " books.");

	    } catch (IOException e) {
	        throw new RuntimeException("Failed to parse CSV: " + e.getMessage());
	    }
	}

	
	public Book updateBook(Long bookId, Book updatedBook) {
        return bookRepository.findById(bookId).map(existingBook -> {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setDescription(updatedBook.getDescription());
            existingBook.setPrice(updatedBook.getPrice());
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setPublishedDate(updatedBook.getPublishedDate());
            existingBook.setStockQuantity(updatedBook.getStockQuantity());
            existingBook.setCategory(updatedBook.getCategory()); //this can also be improved
//            existingBook.setCoverImageUrl(updatedBook.getCoverImageUrl());
            return bookRepository.save(existingBook);
        }).orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
    }

	public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new RuntimeException("Book not found with id: " + bookId);
        }
        bookRepository.deleteById(bookId);
    }

	public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
	public Optional<Book> getBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }

	public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

	public List<Book> searchBooks(String title, String author, String category) {
    	
    	// If no filters are provided, return all books
        if ((title == null || title.isBlank()) &&
            (author == null || author.isBlank()) &&
            (category == null || category.isBlank())) {
            return bookRepository.findAll();
        }
        
        // Very simple search: title OR author OR category contains keyword
        return bookRepository.findByFilters(
                title != null && !title.isBlank() ? title : null,
                        author != null && !author.isBlank() ? author : null,
                        category != null && !category.isBlank() ? category : null
                );
	}
	

	public List<Book> getLowStockBooks(int threshold) {
        return bookRepository.findByStockQuantityLessThan(threshold);
    }

	
}
