package com.bookstore.controller;

import com.bookstore.dto.book.BookRequestDTO;
import com.bookstore.dto.book.BookResponseDTO;
import com.bookstore.service.BookService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

	@Autowired
    private BookService bookService;


	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
	public ResponseEntity<BookResponseDTO> addBook(@Valid @RequestBody BookRequestDTO bookDto) {
	    BookResponseDTO savedBook = bookService.addBook(bookDto);
	    return ResponseEntity.ok(savedBook);
	}

	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-upload")
    public ResponseEntity<String> uploadBooksCsv(@RequestParam("file") MultipartFile file) {
    		bookService.bulkUpload(file);
    		return ResponseEntity.ok("Books uploaded successfully.");
    }
    
	@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{bookId}")
	public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookRequestDTO updatedBookDto) {
	    BookResponseDTO updatedBook = bookService.updateBook(bookId, updatedBookDto);
	    return ResponseEntity.ok(updatedBook);
	}
	
    
	@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
            bookService.deleteBook(bookId);
            return ResponseEntity.ok("Book deleted successfully");
    }
    
  
    
    
    @GetMapping("/get/{bookId}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }
    
    @GetMapping("/get-all")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(
    	    @RequestParam(required = false) String title,
    	    @RequestParam(required = false) String author,
    	    @RequestParam(required = false) String category,
    	    @RequestParam(required = false) String isbn,
    	    @RequestParam(required = false) Double minPrice,
    	    @RequestParam(required = false) Double maxPrice,
    	    @RequestParam(required = false) String sortBy,
    	    @RequestParam(required = false) String sortDir,
    	    @RequestParam(required = false) Boolean available
    	) {
    	    return ResponseEntity.ok(bookService.searchBooks(
    	        title, author, category, isbn, minPrice, maxPrice, sortBy, sortDir, available
    	    ));
    	}

    
    @GetMapping("/get/isbn/{isbn}")
    public ResponseEntity<BookResponseDTO> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/low-stock")
    public ResponseEntity<List<BookResponseDTO>> getLowStockBooks(
            @RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(bookService.getLowStockBooks(threshold));
    }

}
