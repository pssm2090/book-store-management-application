import { Component, OnInit } from '@angular/core';
import { BookControls } from '../../../shared/book-controls/book-controls';
import { CommonModule } from '@angular/common';
import { BookService } from '../../../services/book';
import Swal from 'sweetalert2';

export interface Book {
  bookId: number;
  title: string;
  author: string;
  price: number;
  isbn: string;
  publishedDate: string;
  categoryName: string;
  stockQuantity: number;
  description: string;
  coverImageUrl: string;
}

@Component({
  selector: 'app-trending-books',
  imports: [BookControls, CommonModule],
  templateUrl: './trending-books.html',
  styleUrl: './trending-books.css',
})
export class TrendingBooks implements OnInit {
  loading = false;

  books: Book[] = [];
  originalBooks: Book[] = [];
  filters = {
    category: '',
    priceRange: [0, 1000],
    availability: '',
  };
  sortOption = '';

  constructor(private bookService: BookService) {}

  ngOnInit() {
    window.scrollTo(0, 0);
    this.loadBooks();
  }

 loadBooks() {
    this.loading = true; // Show loader before API call

    this.bookService.getTrendingBooks().subscribe({
      next: (data: { topRated: Book[]; mostPurchased: Book[] }) => {
        // merge both arrays and remove duplicates by bookId
        const merged = [...data.topRated, ...data.mostPurchased];
        const uniqueBooks = merged.filter(
          (book, index, self) =>
            index === self.findIndex((b) => b.bookId === book.bookId)
        );

        this.originalBooks = uniqueBooks;
        this.applyFiltersAndSorting();
        this.loading = false; // Hide loader after success
      },
      error: (err) => {
        console.error(err);
        Swal.fire('Error!', 'Failed to fetch trending books.', 'error');
        this.loading = false; // Hide loader even if error
      },
    });
  }

  onFilterChange(newFilters: any) {
    this.filters = newFilters;
    this.applyFiltersAndSorting();
  }

  onSortChange(option: string) {
    this.sortOption = option;
    this.applyFiltersAndSorting();
  }

  private applyFiltersAndSorting() {
    let filtered = [...this.originalBooks];

    // Category filter
    if (this.filters.category) {
      filtered = filtered.filter(
        (b) => b.categoryName === this.filters.category
      );
    }
    // Availability filter
    if (this.filters.availability) {
      if (this.filters.availability === 'in-stock') {
        filtered = filtered.filter((b) => b.stockQuantity > 0);
      } else if (this.filters.availability === 'out-of-stock') {
        filtered = filtered.filter((b) => b.stockQuantity === 0);
      }
    }
    // Price filter
    filtered = filtered.filter(
      (b) =>
        b.price >= this.filters.priceRange[0] &&
        b.price <= this.filters.priceRange[1]
    );

    // Sorting
    if (this.sortOption === 'price-asc') {
      filtered.sort((a, b) => a.price - b.price);
    } else if (this.sortOption === 'price-desc') {
      filtered.sort((a, b) => b.price - a.price);
    } else if (this.sortOption === 'date-asc') {
      filtered.sort(
        (a, b) =>
          new Date(a.publishedDate).getTime() -
          new Date(b.publishedDate).getTime()
      );
    } else if (this.sortOption === 'date-desc') {
      filtered.sort(
        (a, b) =>
          new Date(b.publishedDate).getTime() -
          new Date(a.publishedDate).getTime()
      );
    }

    this.books = filtered;
  }
}
