import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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
  templateUrl: './edit-books.html',
  styleUrl: './edit-books.css'
})
export class EditBooks implements OnInit {
  book: Book | null = null;
  id: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bookService: BookService
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id') || '';

    if (this.id) { // get book by id api call
      this.bookService.getBookById(this.id).subscribe({
        next: (data) => {
          this.book = data;
        },
        error: () => {
          Swal.fire('Error', 'Failed to fetch book details.', 'error');
        }
      });
    }
  }


  isFormValid(): boolean {
    if (!this.book) return false;
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
      Swal.fire('Validation Error', 'Please fill in all fields correctly.', 'warning');
      return;
    }

    Swal.fire({
      title: 'Save Changes?',
      text: 'This will update the book details permanently.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#2563eb',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, save it!'
    }).then((result) => {
      if (result.isConfirmed) { // update book api call
        this.bookService.updateBook(this.id, this.book).subscribe({
          next: () => {
            Swal.fire('Updated!', 'Book details have been saved.', 'success');
            this.router.navigate(['/']);
          },
          error: () => {
            Swal.fire('Error', 'Failed to update book.', 'error');
          }
        });
      }
    });
  }

  handleCancel(): void {
    this.router.navigate(['/']);
  }
}
