package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

	@Autowired
    private BookService bookService;



    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<String> uploadBooksCsv(@RequestParam("file") MultipartFile file) {
    	try {
    		bookService.bulkUpload(file);
    		return ResponseEntity.ok("Books uploaded successfully.");
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    				.body("Failed to upload books: " + e.getMessage());
    	}
    }
    
    
    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable Long bookId, @RequestBody Book updatedBook) {
            Book savedBook = bookService.updateBook(bookId, updatedBook);
            return ResponseEntity.ok(savedBook);
    }

    
    
    
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
            bookService.deleteBook(bookId);
            return ResponseEntity.ok("Book deleted successfully");
    }
    
  
    
    
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Long bookId) {
    	Optional<Book> book = bookService.getBookById(bookId);
    	return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
            @RequestParam(required = false) String category
    ) {
        List<Book> books = bookService.searchBooks(title, author, category);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.getBookByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<Book>> getLowStockBooks(
            @RequestParam(defaultValue = "5") int threshold) {  // default 5
        List<Book> lowStockBooks = bookService.getLowStockBooks(threshold);
        return ResponseEntity.ok(lowStockBooks);
    }

}
