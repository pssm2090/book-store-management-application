package com.bookstore.repository;

import com.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository 
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);                          
    List<Book> findByStockQuantityLessThan(Integer threshold);
    
    @Query("""
    	    SELECT b FROM Book b
    	    WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
    	      AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))
    	      AND (:category IS NULL OR LOWER(b.category.name) LIKE LOWER(CONCAT('%', :category, '%')))
    	""")
    List<Book> findByFilters(@Param("title") String title,
    	                         @Param("author") String author,
    	                         @Param("category") String category);

}
