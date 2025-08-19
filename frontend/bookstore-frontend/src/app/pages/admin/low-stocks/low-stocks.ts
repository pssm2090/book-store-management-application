import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import Swal from 'sweetalert2';
import { BookService } from '../../../services/book';

export interface LowStockBook {
  bookId: number;
  title: string;
  author: string;
  stockQuantity: number;
  coverImageUrl: string;
}

@Component({
  selector: 'app-low-stocks',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './low-stocks.html',
  styleUrls: ['./low-stocks.css']
})
export class LowStocks implements OnInit {
  lowStockBooks: LowStockBook[] = [];
  loading = false;
  threshold = 5; // default threshold

  constructor(private bookService: BookService) {}

  ngOnInit() {
    this.loadLowStockBooks();
  }

  loadLowStockBooks() {
    this.loading = true;
    this.bookService.getLowStockBooks().subscribe({
      next: (data) => {
        this.lowStockBooks = data;
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error!', 'Failed to fetch low stock books.', 'error');
        this.loading = false;
      },
    });
  }
}
