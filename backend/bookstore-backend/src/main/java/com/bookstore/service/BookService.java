package com.bookstore.service;

import com.bookstore.dto.book.BookRequestDTO;
import com.bookstore.dto.book.BookResponseDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.entity.OrderItem;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;

import jakarta.transaction.Transactional;

import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateISBNException;
import com.bookstore.exception.FileParseException;
import com.bookstore.exception.InsufficientStockException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public BookResponseDTO addBook(BookRequestDTO bookDto) {
        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            throw new DuplicateISBNException("A book with ISBN '" + bookDto.getIsbn() + "' already exists.");
        }

        Category category = categoryRepository.findByNameIgnoreCase(bookDto.getCategory().getName())
                .orElseGet(() -> categoryRepository.save(new Category(bookDto.getCategory().getName())));

        Book book = new Book(
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getDescription(),
                bookDto.getPrice(),
                bookDto.getIsbn(),
                bookDto.getPublishedDate(),
                bookDto.getStockQuantity(),
                category,
                bookDto.getCoverImageUrl()
        );

        return mapToResponse(bookRepository.save(book));
    }

    public void bulkUpload(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            List<String> skippedIsbns = new ArrayList<>();
            List<String> errorRows = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                try {
                    String isbn = record.get("isbn").trim();

                    if (bookRepository.findByIsbn(isbn).isPresent()) {
                        System.err.println("Duplicate ISBN in row: " + record.getRecordNumber() + " -> " + isbn);
                        skippedIsbns.add(isbn);
                        continue;
                    }

                    BookRequestDTO dto = new BookRequestDTO();
                    dto.setTitle(record.get("title").trim());
                    dto.setAuthor(record.get("author").trim());
                    dto.setDescription(record.get("description").trim());
                    dto.setPrice(new BigDecimal(record.get("price").trim()));
                    dto.setIsbn(isbn);
                    dto.setPublishedDate(LocalDate.parse(record.get("publishedDate").trim(), formatter));
                    dto.setStockQuantity(Integer.parseInt(record.get("stockQuantity").trim()));

                    String categoryName = record.get("categoryName").trim();
                    if (categoryName.isEmpty()) {
                        throw new InvalidCategoryException("Category name is missing in CSV row: " + record.getRecordNumber());
                    }

                    Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                            .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

                    dto.setCategory(new Category(category.getName()));

                    dto.setCoverImageUrl(record.get("coverImageUrl").trim());

                    addBook(dto);

                } catch (Exception ex) {
                    System.err.println("Error parsing row: " + record.getRecordNumber() + " -> " + ex.getMessage());
                    errorRows.add("Row " + record.getRecordNumber() + ": " + ex.getMessage());
                }
            }

            System.out.println("Bulk upload completed. Skipped ISBNs: " + skippedIsbns);
            if (!errorRows.isEmpty()) {
                System.out.println("Errors in rows:\n" + String.join("\n", errorRows));
            }

        } catch (IOException e) {
            throw new FileParseException("Failed to parse CSV file.", e);
        }
    }

    public BookResponseDTO updateBook(Long bookId, BookRequestDTO bookDto) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        if (!existingBook.getIsbn().equals(bookDto.getIsbn())) {
            bookRepository.findByIsbn(bookDto.getIsbn()).ifPresent(existing -> {
                throw new DuplicateISBNException("A book with ISBN '" + bookDto.getIsbn() + "' already exists.");
            });
        }

        Category category = categoryRepository.findByNameIgnoreCase(bookDto.getCategory().getName())
                .orElseGet(() -> categoryRepository.save(new Category(bookDto.getCategory().getName())));

        existingBook.setTitle(bookDto.getTitle());
        existingBook.setAuthor(bookDto.getAuthor());
        existingBook.setDescription(bookDto.getDescription());
        existingBook.setPrice(bookDto.getPrice());
        existingBook.setIsbn(bookDto.getIsbn());
        existingBook.setPublishedDate(bookDto.getPublishedDate());
        existingBook.setStockQuantity(bookDto.getStockQuantity());
        existingBook.setCategory(category);
        existingBook.setCoverImageUrl(bookDto.getCoverImageUrl());

        return mapToResponse(bookRepository.save(existingBook));
    }

    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book not found with id: " + bookId);
        }
        bookRepository.deleteById(bookId);
    }

    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BookResponseDTO getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));
        return mapToResponse(book);
    }

    public BookResponseDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        return mapToResponse(book);
    }

    public List<BookResponseDTO> searchBooks(
            String title, String author, String category,
            String isbn, Double minPrice, Double maxPrice,
            String sortBy, String sortDir, Boolean available) {

        Sort.Direction direction = (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        String sortField = (sortBy != null && (sortBy.equalsIgnoreCase("price") || sortBy.equalsIgnoreCase("publishedDate")))
                ? sortBy : "title";

        Sort sort = Sort.by(direction, sortField);

        return bookRepository.findByFilters(
                isBlank(title) ? null : title,
                isBlank(author) ? null : author,
                isBlank(category) ? null : category,
                isBlank(isbn) ? null : isbn,
                minPrice,
                maxPrice,
                available != null && available,
                sort
        ).stream().map(this::mapToResponse).toList();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public List<BookResponseDTO> getLowStockBooks(int threshold) {
        return bookRepository.findByStockQuantityLessThan(threshold)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deductStockAfterOrder(Long bookId, int quantity, boolean isReversal) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        int updatedStock = book.getStockQuantity() - quantity;

        if (updatedStock < 0) {
            throw new InsufficientStockException("Not enough stock for book: " + book.getTitle());
        }

        book.setStockQuantity(updatedStock);
        bookRepository.save(book);
    }

    @Transactional
    public void restoreStockAfterCancellationOrReturn(List<OrderItem> items) {
        for (OrderItem item : items) {
            Book book = item.getBook();
            int updatedStock = book.getStockQuantity() + item.getQuantity();
            book.setStockQuantity(updatedStock);
            bookRepository.save(book);
        }
    }

    private BookResponseDTO mapToResponse(Book book) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication != null && authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        return new BookResponseDTO(book, isAdmin);
    }
}
