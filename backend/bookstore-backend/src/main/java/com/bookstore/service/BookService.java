package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateISBNException;
import com.bookstore.exception.FileParseException;
import com.bookstore.exception.InvalidCategoryException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BookService {

	@Autowired
    private BookRepository bookRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	public Book addBook(Book book) {
		if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateISBNException("A book with ISBN '" + book.getIsbn() + "' already exists.");
        }
		
		if(book.getCategory() != null && book.getCategory().getName() != null) {
			String categoryName = book.getCategory().getName();
			
			Category category = categoryRepository.findByNameIgnoreCase(categoryName)
					.orElseGet(() -> categoryRepository.save(new Category(categoryName)));
			book.setCategory(category);
		} else {
			throw new InvalidCategoryException("Category name must be provided.");
		}
        return bookRepository.save(book);
    }
	
	public void bulkUpload(MultipartFile file) {
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
	         CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

	        List<Book> books = new ArrayList<>();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    		List<String> skippedIsbns = new ArrayList<>();

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
	        		if (categoryName.isEmpty()) {
                        throw new InvalidCategoryException("Category name is missing in CSV row: " + record.getRecordNumber());
                    }
	        		Category category = categoryRepository.findByNameIgnoreCase(categoryName)
	                                 .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
	        		book.setCategory(category);


//	            	book.setCoverImageUrl(record.get("coverImageUrl"));
	        		
	        		if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
	        		    System.err.println("Duplicate ISBN in row: " + record.getRecordNumber() + " -> " + book.getIsbn());
	        		    skippedIsbns.add(book.getIsbn());
	        		    continue;
	        		}
	        		
	        		
	        		books.add(book);
	        	} catch (Exception ex) {
	                System.err.println("Error parsing row: " + record.getRecordNumber() + " -> " + ex.getMessage());
	            }
	        }

	        bookRepository.saveAll(books);
	        System.out.println("Successfully saved " + books.size() + " books.");

	        if (!skippedIsbns.isEmpty()) {
	            System.out.println("Skipped duplicate ISBNs: " + String.join(", ", skippedIsbns));
	        }
	        
	    } catch (IOException e) {
	        throw new FileParseException("Failed to parse CSV file.", e);
	    }
	}

	
	public Book updateBook(Long bookId, Book updatedBook) {
		Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

		if (!existingBook.getIsbn().equals(updatedBook.getIsbn())) {
		    bookRepository.findByIsbn(updatedBook.getIsbn()).ifPresent(existing -> {
		        throw new DuplicateISBNException("A book with ISBN '" + updatedBook.getIsbn() + "' already exists.");
		    });
		}

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPublishedDate(updatedBook.getPublishedDate());
        existingBook.setStockQuantity(updatedBook.getStockQuantity());
        existingBook.setCategory(updatedBook.getCategory());

        return bookRepository.save(existingBook);
    }

	public void deleteBook(Long bookId) {
		if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book not found with id: " + bookId);
        }
        bookRepository.deleteById(bookId);
    }

	public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
	public Book getBookById(Long bookId) {
		return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));
    }

	public Book getBookByIsbn(String isbn) {
		return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    }

	public List<Book> searchBooks(String title, String author, String category,
	                              String isbn, Double minPrice, Double maxPrice,
	                              String sortBy, String sortDir) {

	    Sort.Direction direction = (sortDir != null && sortDir.equalsIgnoreCase("desc"))
	            ? Sort.Direction.DESC : Sort.Direction.ASC;

	    String sortField = (sortBy != null && (sortBy.equalsIgnoreCase("price") || sortBy.equalsIgnoreCase("publishedDate")))
	            ? sortBy : "title"; // Default sort by title

	    Sort sort = Sort.by(direction, sortField);

	    return bookRepository.findByFilters(
	            isBlank(title) ? null : title,
	            isBlank(author) ? null : author,
	            isBlank(category) ? null : category,
	            isBlank(isbn) ? null : isbn,
	            minPrice,
	            maxPrice,
	            sort
	    );
	}

	private boolean isBlank(String value) {
	    return value == null || value.trim().isEmpty();
	}

	

	public List<Book> getLowStockBooks(int threshold) {
        return bookRepository.findByStockQuantityLessThan(threshold);
    }

	
}
