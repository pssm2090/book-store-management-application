import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { BookService } from '../../../services/book';

interface Book {
  title: string;
  author: string;
  price: number;
  isbn: string;
  publishedDate: string;
  category: {
    name: string;
  };
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
  title: '',
  author: '',
  price: 0,
  isbn: '',
  publishedDate: '',
  category: {
    name: ''
  },
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
    this.book.publishedDate !== '' &&
    this.book.category.name.trim() !== '' &&  
    this.book.description.trim() !== '' &&
    this.book.coverImageUrl.trim() !== '' &&
    this.book.stockQuantity > 0
  );
}


  handleSave(): void {
    if (!this.isFormValid()) {
      Swal.fire('Error', 'Please fill in all fields before saving.', 'error');
      return;
    }

    // Convert to dd-MM-yyyy format
  const dateObj = new Date(this.book.publishedDate);
  const day = String(dateObj.getDate()).padStart(2, '0');
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const year = dateObj.getFullYear();
  const formattedDate = `${day}-${month}-${year}`;

    const formattedBook = {
    ...this.book,
    publishedDate: formattedDate
  };

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
        this.bookService.addBook(formattedBook).subscribe({
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
