import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BookService } from '../../../services/book';
import { CartService } from '../../../services/cart';
import { Auth } from '../../../services/auth';
import Swal from 'sweetalert2';

interface Review {
  id: number;
  userEmail: string;
  name: string;
  rating: number;
  date: string;
  comment: string | null;
}

@Component({
  imports: [FormsModule, CommonModule],
  selector: 'app-book-detail',
  templateUrl: './book-detail.html',
  styleUrls: ['./book-detail.css'],
})
export class BookDetail implements OnInit {
  id!: string;
  book: any;
  reviews: Review[] = [];
  user: any = null;
  newRating: number = 5;
  newComment: string = '';
  error: string = '';
  success: string = '';
  quantity: number = 1;

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cartService: CartService,
    private auth: Auth
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id') || '';
    this.loadBook();
    this.loadReviews();

    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      this.user = JSON.parse(storedUser);
    }

    window.scrollTo(0, 0);
  }

  loadBook() {
    Swal.fire({
      title: 'Loading book...',
      allowOutsideClick: false,
      didOpen: () => Swal.showLoading(),
    });

    this.bookService.getBookById(this.id).subscribe({
      next: (data) => {
        this.book = data;
        Swal.close();
      },
      error: (err) => {
        Swal.fire('Error!', 'Failed to load book details', 'error');
        console.error('Book load error:', err);
      },
    });
  }

  loadReviews() {
    this.bookService.getReviewsUsingBookId(this.id).subscribe({
      next: (data: any[]) => {
        this.reviews = data.map((r) => ({
          id: r.reviewId,
          userEmail: r.user?.email,
          name: r.user?.name || 'Anonymous',
          rating: r.rating,
          date: new Date(r.reviewDate).toISOString().slice(0, 10),
          comment: r.comment,
        }));
      },
      error: (err) => {
        const message = err.error?.message || 'Failed to load reviews';
        Swal.fire('Error!', message, 'error');
        console.error('Review load error:', err);
      },
    });
  }

  get averageRating(): string {
    if (this.reviews.length === 0) return '0';
    return (
      this.reviews.reduce((sum, r) => sum + r.rating, 0) / this.reviews.length
    ).toFixed(1);
  }

  handleSubmitReview() {
    this.error = '';
    this.success = '';

    if (!this.user || !this.auth.isLoggedIn()) {
      this.error = 'You must be logged in to leave a review.';
      return;
    }

    if (this.hasUserReviewed()) {
      this.error = 'You have already reviewed this book.';
      return;
    }

    if (this.newComment.trim() === '') {
      this.error = 'Please enter a comment.';
      return;
    }

    if (isNaN(this.newRating) || this.newRating < 0.5 || this.newRating > 5) {
      this.error = 'Rating must be between 0.5 and 5.';
      return;
    }

    const payload = {
      rating: this.newRating,
      comment: this.newComment.trim(),
    };

    this.bookService.addReview(payload, this.id).subscribe({
      next: () => {
        Swal.fire('Success!', 'Your review has been added.', 'success');
        this.newComment = '';
        this.newRating = 5;
        this.loadReviews(); // ✅ Reload reviews from backend
      },
      error: () => Swal.fire('Error!', 'Failed to save review', 'error'),
    });
  }

  deleteReview(reviewId: number) {
  Swal.fire({
    title: 'Are you sure?',
    text: 'Do you want to delete your review?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Yes, delete it!',
    cancelButtonText: 'Cancel',
  }).then((result) => {
    if (result.isConfirmed) {
      this.bookService.deleteReview(reviewId).subscribe({
        next: (response) => {
          if (!response || response.status === 200 || response.status === 204) {
            this.reviews = this.reviews.filter((r) => r.id !== reviewId);
            Swal.fire('Deleted!', 'Your review has been deleted.', 'success');
          }
        },
        error: (err) => {
          console.error('Delete review failed', err);
          Swal.fire('Error!', 'Failed to delete review.', 'error');
        },
      });
    }
  });
}


  hasUserReviewed(): boolean {
    return this.reviews.some((r) => r.userEmail === this.user?.email);
  }

  handleAddToCart() {
    if (!this.book) return;

    if (this.quantity < 1 || this.quantity > this.book.stockQuantity) {
      Swal.fire('Error!', 'Please enter a valid quantity.', 'error');
      return;
    }

    this.cartService.addToCart(this.id, this.quantity).subscribe({
      next: () => {
        Swal.fire(
          'Added!',
          `${this.quantity} × "${this.book.title}" added to cart.`,
          'success'
        );
      },
      error: () => {
        Swal.fire('Error!', 'Failed to add to cart.', 'error');
      },
    });
  }
}
