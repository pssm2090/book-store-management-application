import { Component, OnInit } from '@angular/core';
import { BookControls } from '../../../shared/book-controls/book-controls';
import { CommonModule } from '@angular/common';

interface Book {
  id: number;
  title: string;
  author: string;
  category: string;
  price: number;
  availability: string;
  publishDate: string;
  image: string;
  rating: number;
}

@Component({
  selector: 'app-recommended-books',
  imports: [CommonModule, BookControls],
  templateUrl: './recommended-books.html',
  styleUrl: './recommended-books.css'
})

export class RecommendedBooks implements OnInit {

  initialBooks: Book[] = [
    {
      id: 1,
      title: 'The Great Gatsby',
      author: 'F. Scott Fitzgerald',
      category: 'Fiction',
      price: 299,
      availability: 'in-stock',
      publishDate: '2022-03-10',
      image: 'https://www.bookswagon.com/productimages/images200/862/9780190635862.jpg',
      rating: 4.5
    },
    {
      id: 2,
      title: 'To Kill a Mockingbird',
      author: 'Harper Lee',
      category: 'Fiction',
      price: 350,
      availability: 'out-of-stock',
      publishDate: '2021-05-12',
      image: 'https://images-na.ssl-images-amazon.com/images/I/71kxa1-0mfL.jpg',
      rating: 4.5
    },
    {
      id: 3,
      title: '1984',
      author: 'George Orwell',
      category: 'Sci-Fi',
      price: 280,
      availability: 'in-stock',
      publishDate: '2023-01-25',
      image: 'https://images-na.ssl-images-amazon.com/images/I/71kxa1-0mfL.jpg',
      rating: 4.5
    }
  ];

  books: Book[] = [];
  filters = {
    category: '',
    priceRange: [0, 1000],
    availability: ''
  };
  sortOption = '';

  ngOnInit() {
    window.scrollTo(0, 0);
    this.applyFiltersAndSorting();
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
    let filtered = [...this.initialBooks];

    // Category filter
    if (this.filters.category) {
      filtered = filtered.filter(b => b.category === this.filters.category);
    }
    // Availability filter
    if (this.filters.availability) {
      filtered = filtered.filter(b => b.availability === this.filters.availability);
    }
    // Price filter
    filtered = filtered.filter(
      b => b.price >= this.filters.priceRange[0] && b.price <= this.filters.priceRange[1]
    );

    // Sorting
    if (this.sortOption === 'price-asc') {
      filtered.sort((a, b) => a.price - b.price);
    } else if (this.sortOption === 'price-desc') {
      filtered.sort((a, b) => b.price - a.price);
    } else if (this.sortOption === 'date-asc') {
      filtered.sort(
        (a, b) => new Date(a.publishDate).getTime() - new Date(b.publishDate).getTime()
      );
    } else if (this.sortOption === 'date-desc') {
      filtered.sort(
        (a, b) => new Date(b.publishDate).getTime() - new Date(a.publishDate).getTime()
      );
    }

    this.books = filtered;
  }
}
