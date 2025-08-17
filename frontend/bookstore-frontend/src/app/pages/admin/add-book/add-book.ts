import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { BookService } from '../../../services/book';

interface Book {
  id: string;
  title: string;
  author: string;
  price: number;
  isbn: string;
  publishDate: string;
  categoryName: string;
  description: string;
  coverImageUrl: string;
  stockQuantity: number;
}

@Component({
  selector: 'app-edit-book',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-book.html',
  styleUrl: './add-book.css',
})
export class AddBook implements OnInit {
  book: Book = {
    id: '',
    title: '',
    author: '',
    price: 0,
    isbn: '',
    publishDate: '',
    categoryName: '',
    description: '',
    coverImageUrl: '',
    stockQuantity: 0
  };

  constructor(private router: Router, private bookService: BookService) {}

  ngOnInit(): void {
    // Nothing to load for add mode
  }

  private isFormValid(): boolean {
    return (
      this.book.title.trim() !== '' &&
      this.book.author.trim() !== '' &&
      this.book.price > 0 &&
      this.book.isbn.trim() !== '' &&
      this.book.publishDate.trim() !== '' &&
      this.book.categoryName.trim() !== '' &&
      this.book.description.trim() !== '' &&
      this.book.coverImageUrl.trim() !== '' &&
      this.book.stockQuantity >0
    );
  }

  handleSave(): void {
    if (!this.isFormValid()) {
      Swal.fire('Error', 'Please fill in all fields before saving.', 'error');
      return;
    }

    Swal.fire({
      title: 'Save Changes?',
      text: 'This will update the book details permanently.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#2563eb',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, save it!',
    }).then((result) => {
      if (result.isConfirmed) {
        // add book api call
        this.bookService.addBook(this.book).subscribe({
          next: () => {
            Swal.fire('Added!', 'The new book has been saved.', 'success');
            this.router.navigate(['/admin']);
          },
          error: () => {
            Swal.fire('Error', 'Failed to save the book.', 'error');
          },
        });
      }
    });
  }

  handleCancel(): void {
    this.router.navigate(['/']);
  }
}
