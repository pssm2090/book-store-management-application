import { Routes } from '@angular/router';

import { Home } from './pages/home/home/home';
import { BookDetail } from './pages/book-detail/book-detail/book-detail';
import { Login } from './pages/login/login/login'; 
import { Register } from './pages/register/register/register';
import { Cart } from './pages/cart/cart/cart';
import { Profile } from './pages/profile/profile/profile';
import { AllBooks } from './pages/books/all-books/all-books';
import { RecommendedBooks } from './pages/books/recommended-books/recommended-books';
import { TrendingBooks } from './pages/books/trending-books/trending-books';
import { EditBooks } from './pages/admin/edit-books/edit-books';
import { AddBook } from './pages/admin/add-book/add-book';
import { LowStocks } from './pages/admin/low-stocks/low-stocks';
import { StatsComponent } from './pages/admin/stats/stats';
import { AuthGuard } from './auth-guard';
import { Analytics } from './pages/analytics/analytics';

export const routes: Routes = [
 { path: '', component: Home },
  { path: 'book/:id', component: BookDetail },
  { path: 'login', component: Login },
  { path: 'register', component: Register },

  // Protected for logged-in users
  { path: 'cart', component: Cart, canActivate: [AuthGuard] },
  { path: 'profile', component: Profile, canActivate: [AuthGuard] },

  // Books
  { path: 'books/all', component: AllBooks },
  { path: 'books/recommended', component: RecommendedBooks },
  { path: 'books/trending', component: TrendingBooks },

  // Admin (role based)
  { path: 'admin/edit-book/:id', component: EditBooks, canActivate: [AuthGuard]},
  { path: 'admin/add-book', component: AddBook, canActivate: [AuthGuard] },
  { path: 'admin/books/low-stock', component: LowStocks, canActivate: [AuthGuard] },
  { path: 'admin/stats', component: StatsComponent, canActivate: [AuthGuard] },
  { path: 'admin/analytics', component: Analytics, canActivate: [AuthGuard] },

  { path: '**', redirectTo: '', pathMatch: 'full' },
];
