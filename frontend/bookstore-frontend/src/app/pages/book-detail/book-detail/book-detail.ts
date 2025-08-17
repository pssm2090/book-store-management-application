import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BookService } from '../../../services/book';
import Swal from 'sweetalert2';
import { CartService } from '../../../services/cart';

interface Review {
  id: number;
  userEmail: string;
  name: string;
  rating: number;
  date: string;
  comment: string;
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
  user: any;
  newRating: number = 5;
  newComment: string = '';
  error: string = '';
  success: string = '';

  quantity: number = 1; // ✅ default quantity

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id') || '';
    this.loadBook();
    this.loadReviews();

    // Simulated logged-in user
    this.user = {
      email: 'sovan@bookstore.com', // from JWT
      name: 'Sovan Roy',
      orderHistory: [2, 3, 4, 12],
    };

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
        console.log(data)
        console.log('📘 Book ID:', this.book.id);
        console.log('🛒 User orderHistory:', this.user?.orderHistory);
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
          userEmail: r.user?.email, // ✅ store email
          name: r.user?.name || 'Anonymous',
          rating: r.rating,
          date: new Date(r.reviewDate).toISOString().slice(0, 10),
          comment: r.comment,
        }));
      },
      error: (err) => {
        Swal.fire('Error!', 'Failed to load reviews', 'error');
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

    if (!this.user.orderHistory.includes(this.book.id)) {
      this.error = 'You can only review books you have purchased.';
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

    // ✅ Frontend Review Object (for UI)
    const newReview: Review = {
      id: this.reviews.length + 1,
      userEmail: this.user.email,
      name: this.user.name,
      rating: this.newRating,
      date: new Date().toISOString().slice(0, 10),
      comment: this.newComment.trim(),
    };

    // Prepend to UI
    this.reviews = [newReview, ...this.reviews];

    // ✅ API Payload (as backend expects)
    const payload = {
      rating: this.newRating,
      coment: this.newComment.trim(), // spelling matches backend
    };

    // Call API
    this.bookService.addReview(payload, this.book.id).subscribe({
      next: () => {
        Swal.fire('Success!', 'Your review has been added.', 'success');
        this.newComment = '';
        this.newRating = 5;
      },
      error: () => Swal.fire('Error!', 'Failed to save review', 'error'),
    });
  }

  deleteReview(review: Review) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'Do you want to delete your review?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        // Call API to delete review
        this.bookService.deleteReview(this.id).subscribe({
          next: () => {
            this.reviews = this.reviews.filter((r) => r.id !== review.id);
            Swal.fire('Deleted!', 'Your review has been deleted.', 'success');
          },
          error: () => {
            Swal.fire('Error!', 'Failed to delete review.', 'error');
          },
        });
      }
    });
  }

  hasUserReviewed(): boolean {
    return this.reviews.some((r) => r.userEmail === this.user.email);
  }

  // ✅ Add to cart function
  handleAddToCart() {
    if (!this.book) return;


    if (this.quantity < 1 || this.quantity > this.book.stockQuantity) {
      Swal.fire('Error!', 'Please enter a valid quantity.', 'error');
      return;
    }

    this.cartService.addToCart(this.book.id, this.quantity).subscribe({
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
