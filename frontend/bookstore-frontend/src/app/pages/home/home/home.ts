import { Component } from '@angular/core';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BookService } from '../../../services/book';
import { UserService } from '../../../services/user';
import { Auth } from '../../../services/auth';
import { OrderService } from '../../../services/order';
import { FormsModule } from '@angular/forms';

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

export interface User {
  accessToken: string;
  refreshToken: string;
  message: number;
  email: string;
  name: string;
  role: string;
}

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
})
export class Home {
  books: Book[] = [];
  orders: any[] = [];
  recommendedBooks: Book[] = [];
  trendingBooks: Book[] = [];
  lowStockBooks: Book[] = [];
  users: User[] = [];
  searchResults: any[] = [];
  stockThreshold = 5;
  loading = false;

  topRatedBooks: any[] = [];
  mostPurchasedBooks: any[] = [];

  constructor(
    private router: Router,
    private bookService: BookService,
    private userService: UserService,
    private authService: Auth,
    private orderService: OrderService
  ) {}

  // ✅ Getters
  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get userRole(): string | null {
    return this.authService.getUserRole();
  }

  get isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  // ✅ Central confirm helper
  private async confirmAction(
    title: string,
    text: string,
    confirmText: string,
    actionFn: () => void
  ) {
    const result = await Swal.fire({
      title,
      text,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#2563eb',
      cancelButtonColor: '#d33',
      confirmButtonText: confirmText,
    });
    if (result.isConfirmed) {
      actionFn();
    }
  }

  // ✅ Load books (with admin/user distinction)
  loadBooks() {
    this.loading = true;
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = this.isAdmin ? data : data.slice(0, 4);
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error!', 'Failed to fetch books.', 'error');
        this.loading = false;
      },
    });
  }

  // ✅ Delete book
  deleteBook(bookId: number, bookTitle: string) {
  this.confirmAction(
    'Delete Book?',
    `Are you sure you want to delete "${bookTitle}"? This action cannot be undone.`,
    'Yes, delete it!',
    () => {
      this.bookService.deleteBook(bookId).subscribe({
        next: (response) => {
          if (response.status === 200 || response.status === 204) {
            Swal.fire('Deleted!', 'The book has been deleted.', 'success');
            this.loadBooks();
          } else {
            Swal.fire('Error!', 'Unexpected response from server.', 'error');
          }
        },
        error: (error) => {
          console.error('Delete API Error:', error);
          Swal.fire('Error!', 'Failed to delete book.', 'error');
        },
      });
    }
  );
}


  // ✅ Navigation
  addBook() {
    this.router.navigate([`/admin/add-book`]);
  }

  editBook(bookId: number) {
    this.router.navigate([`/admin/edit-book/${bookId}`]);
  }

  // ✅ Bulk upload
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];

    this.confirmAction(
      'Bulk Upload Books?',
      `You are about to upload: ${file.name}. Continue?`,
      'Yes, upload!',
      () => {
        this.bookService.bulkUploadBooks(file).subscribe({
          next: () => {
            Swal.fire('Success!', 'Books uploaded successfully.', 'success');
            this.loadBooks();
          },
          error: (error) => {
            console.error('Bulk Upload Error:', error);
            Swal.fire('Error!', 'Failed to upload books.', 'error');
          },
        });
      }
    );
  }

  // ✅ Low stock books
  loadLowStockBooks() {
    this.bookService.getLowStockBooks().subscribe({
      next: (data) => {
        console.log(data)
        this.lowStockBooks = data;
        if (this.isAdmin && this.lowStockBooks.length > 0) {
          setTimeout(() => {
            Swal.fire({
              title: 'Low Stock Alert 🚨',
              html: this.lowStockBooks
                .map((b) => `<b>${b.title}</b> (Stock: ${b.stockQuantity})`)
                .join('<br>'),
              icon: 'warning',
            });
          }, 3000);
        }
      },
      error: () => console.error('Failed to fetch low stock books'),
    });
  }

  // ✅ Users
  loadUsers() {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        Swal.fire('Error!', 'Failed to fetch users.', 'error');
        console.error('User fetch error:', err);
      },
    });
  }

  // ✅ Orders
  loadOrders() {
    this.loading = true;
    this.orderService.getOrdersAdmin().subscribe({
      next: (data) => {
        this.orders = data;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        Swal.fire('Error!', 'Failed to fetch orders.', 'error');
        console.error('Orders fetch error:', err);
      },
    });
  }

  updateStatus(orderId: number, newStatus: string) {
    this.orderService.updateOrderStatus(orderId, newStatus).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Updated!',
          text: `Order #${orderId} marked as ${newStatus}.`,
          timer: 2000,
          showConfirmButton: false,
        });
      },
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Could not update order status. Please try again.',
        });
      },
    });
  }

  // fetch recommended books
  loadRecommendedBooks() {
    this.bookService.getRecommendedBooks().subscribe({
      next: (data) => {
        this.recommendedBooks = data;
      },
      error: (err) => {
        console.error('Failed to fetch recommended books', err);
      },
    });
  }
 
  // fetch trending books
  loadTrendingBooks() {
    this.bookService.getTrendingBooks().subscribe({
      next: (res) => {
        this.topRatedBooks = res.topRated || [];
        this.mostPurchasedBooks = res.mostPurchased || [];
      },
      error: (err) => {
        console.error('Failed to fetch trending books', err);
      },
    });
  }

  // ✅ ngOnInit
  ngOnInit() {
    this.loadBooks();
    if(this.isAdmin)
      this.loadUsers();

     if(this.isAdmin)
      this.loadOrders();

    if(this.isAdmin)
      this.loadLowStockBooks();

    if(!this.isAdmin)
    this.loadRecommendedBooks();
  
  if(!this.isAdmin)
    this.loadTrendingBooks();

    // Search handler
    this.bookService.searchEvent$.subscribe((event) => {
      if (!event) return;
      this.loading = true;

      let apiCall;
      switch (event.type) {
        case 'title':
          apiCall = this.bookService.searchBooksByTitle(event.query);
          break;
        case 'author':
          apiCall = this.bookService.searchBooksByAuthor(event.query);
          break;
        case 'isbn':
          apiCall = this.bookService.searchBooksByIsbn(event.query);
          break;
      }

      if (apiCall) {
        apiCall.subscribe({
          next: (data) => {
            this.searchResults = data;
            this.loading = false;
          },
          error: () => {
            Swal.fire('Error!', 'Failed to fetch search results.', 'error');
            this.loading = false;
          },
        });
      } else {
        this.loading = false;
      }
    });
  }
}




