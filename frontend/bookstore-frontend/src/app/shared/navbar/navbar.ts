import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BookService } from '../../services/book';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  // isLoggedIn = false; // Replace with AuthService later


  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get userRole(): string | null {
    return this.authService.getUserRole();
  }

  get userName(): string | null {
    return this.authService.getUserName();
  }

  searchType = 'title';
  searchQuery = '';

  constructor(private bookService: BookService, private authService: Auth) {}

  onSearch() {
    if (this.searchQuery.trim()) {
      this.bookService.triggerSearch(this.searchType, this.searchQuery);
    }
  }
}

