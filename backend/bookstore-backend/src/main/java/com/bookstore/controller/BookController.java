package com.bookstore.controller;

import com.bookstore.entity.Book;
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
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return ResponseEntity.ok(savedBook);
    }

	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-upload")
    public ResponseEntity<String> uploadBooksCsv(@RequestParam("file") MultipartFile file) {
    		bookService.bulkUpload(file);
    		return ResponseEntity.ok("Books uploaded successfully.");
    }
    
	@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable Long bookId, @Valid @RequestBody Book updatedBook) {
            Book savedBook = bookService.updateBook(bookId, updatedBook);
            return ResponseEntity.ok(savedBook);
    }
	
    
	@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
            bookService.deleteBook(bookId);
            return ResponseEntity.ok("Book deleted successfully");
    }
    
  
    
    
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Long bookId) {
    	Book book = bookService.getBookById(bookId);
    	return ResponseEntity.ok(book);
    }
    
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
    	List<Book> books = bookService.getAllBooks();
    	return ResponseEntity.ok(books);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,       // "price" or "publishedDate"
            @RequestParam(required = false) String sortDir       // "asc" or "desc"
    ) {
        List<Book> books = bookService.searchBooks(
            title, author, category, isbn, minPrice, maxPrice, sortBy, sortDir
        );
        return ResponseEntity.ok(books);
    }

    
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
    	Book book = bookService.getBookByIsbn(isbn);
    	return ResponseEntity.ok(book);
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<Book>> getLowStockBooks(
            @RequestParam(defaultValue = "5") int threshold) {  // default 5
        List<Book> lowStockBooks = bookService.getLowStockBooks(threshold);
        return ResponseEntity.ok(lowStockBooks);
    }

}
