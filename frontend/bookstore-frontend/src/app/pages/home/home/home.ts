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
  id: number;
  title: string;
  author: string;
  price: number;
  isbn: string;
  publishDate: string;
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
  imports: [CommonModule, RouterModule,FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
})
export class Home {
  onStockBlur(
    arg0: string,
    _t30: HTMLInputElement,
    _t17: {
      id: number;
      title: string;
      author: string;
      price: number;
      image: string;
      rating: number;
    }
  ) {
    throw new Error('Method not implemented.');
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get userRole(): string | null {
    return this.authService.getUserRole();
  }


  books: Book[] = [];
  orders: any[] = []; // store all orders

  get isAdmin(): boolean {
  return this.authService.getUserRole() === 'ADMIN';
}



  lowStockBooks: Book[] = [];
  stockThreshold = 5; // you can change this to any limit

  users: User[] = [];   // now comes from API

  searchResults: any[] = [];

 constructor(
  private router: Router,
  private bookService: BookService,
  private userService: UserService,
  private authService: Auth,
  private orderService: OrderService   // 👈 add this
) {}


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

  loading = false;

  // get all books api
  loadBooks() {
    this.loading = true;
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data;
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error!', 'Failed to fetch books.', 'error');
        this.loading = false;
      },
    });
  }

  // delete book by id api
  deleteBook(bookId: number, bookTitle: string) {
    this.confirmAction(
      'Delete Book?',
      `Are you sure you want to delete "${bookTitle}"? This action cannot be undone.`,
      'Yes, delete it!',
      () => {
        this.bookService.deleteBook(bookId).subscribe({
          next: (response) => {
            Swal.fire('Deleted!', 'The book has been deleted.', 'success');
            this.loadBooks(); // Refresh book list after deletion
          },
          error: (error) => {
            console.error('Delete API Error:', error); // <-- Log error details
            Swal.fire('Error!', 'Failed to delete book.', 'error');
          },
        });
      }
    );
  }

  addBook() {
    this.router.navigate([`/admin/add-book`]);
  }

  editBook(bookId: number) {
    this.router.navigate([`/admin/edit-book/${bookId}`]);
  }

  // bulk upload api
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];

    // Confirm before upload
    this.confirmAction(
      'Bulk Upload Books?',
      `You are about to upload: ${file.name}. Continue?`,
      'Yes, upload!',
      () => {
        this.bookService.bulkUploadBooks(file).subscribe({
          next: (response) => {
            console.log('Bulk Upload Response:', response); // <-- See backend response
            Swal.fire('Success!', 'Books uploaded successfully.', 'success');
            this.loadBooks(); // Refresh list
          },
          error: (error) => {
            console.error('Bulk Upload Error:', error); // <-- Log full error
            Swal.fire('Error!', 'Failed to upload books.', 'error');
          },
        });
      }
    );
  }

  // low stock api
  loadLowStockBooks() {
    this.bookService.getLowStockBooks(this.stockThreshold).subscribe({
      next: (data) => {
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
          }, 3000); // 3 seconds delay
        }
      },
      error: () => {
        console.error('Failed to fetch low stock books');
      },
    });
  }

  // get all users for admin
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

  // search api call
  ngOnInit() {
    this.loadBooks();
    this.loadUsers();
    this.loadOrders();   
    // Fetch books from API when home page loads
    this.loading = true;
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        if (!this.isAdmin) this.books = data.slice(0, 4);
        else this.books = data;
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error!', 'Failed to fetch books.', 'error');
        this.loading = false;
      },
    });

    this.bookService.searchEvent$.subscribe((event) => {
      if (!event) return;

      this.loading = true; // <-- START loading before making the API call

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
            this.loading = false; // <-- STOP loading when data arrives
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

    this.loadLowStockBooks();
  }

  // get all orders for admin
loadOrders() {
  this.loading = true;
  this.orderService.getOrdersAdmin().subscribe({
    next: (data) => {
      this.orders = data;  // your API already returns array of orders
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
          showConfirmButton: false
        });
      },
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Could not update order status. Please try again.',
        });
      }
    });
  }

}
