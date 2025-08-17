import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookService } from '../../services/book';
import Swal from 'sweetalert2';

interface Filters {
  category: string;
  priceRange: [number, number];
  availability: string;
}

interface Category {
  categoryId: number;
  name: string;
}

@Component({
  selector: 'app-book-controls',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book-controls.html',
  styleUrl: './book-controls.css',
})
export class BookControls implements OnInit {
  @Output() filterChange = new EventEmitter<Filters>();
  @Output() sortChange = new EventEmitter<string>();

  filters: Filters = {
    category: '',
    priceRange: [0, 1000],
    availability: '',
  };

  sortOption = '';
  categories: string[] = []; // Will be filled from API

  constructor(private bookService: BookService) {}

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories() {
    this.bookService.getAllCategories().subscribe({
      next: (data: Category[]) => {
        // console.log(data);
        this.categories = data.map((category) => category.name); // ✅ extract names
      },
      error: (err) => {
        console.error(err);
        Swal.fire('Error!', 'Failed to fetch categories.', 'error');
      },
    });
  }

  onFilterChange(name: keyof Filters, value: string) {
    (this.filters as any)[name] = value;
    this.filterChange.emit({ ...this.filters });
  }

  onPriceRangeChange(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    const [min, max] = value.split('-').map((v) => parseInt(v, 10));
    this.filters.priceRange = [min, max];
    this.filterChange.emit({ ...this.filters });
  }

  onSortChange(value: string) {
    this.sortOption = value;
    this.sortChange.emit(value);
  }

  onCustomPriceChange() {
  const [min, max] = this.filters.priceRange;
  
  // Prevent invalid values (like max < min)
  if (min > max) {
    this.filters.priceRange[1] = min;
  }

  this.filterChange.emit({ ...this.filters });
}

}
