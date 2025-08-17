import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private apiUrl = 'https://api.example.com/books'; // change to your API

  // BehaviorSubject to store search request
  private searchEventSource = new BehaviorSubject<{
    type: string;
    query: string;
  } | null>(null);
  searchEvent$ = this.searchEventSource.asObservable();

  constructor(private http: HttpClient) {}

  triggerSearch(type: string, query: string) {
    this.searchEventSource.next({ type, query });
  }

  // GET all books
  getAllBooks(): Observable<any> {
    // return this.http.get(`${this.apiUrl}/categories/get-all`);
    return this.http.get(`https://dummyjson.com/c/05eb-5a96-415c-81ed`);
  }

  // GET book by ID
  getBookById(id: string): Observable<any> {
    // return this.http.get(`${this.apiUrl}/${id}`);
    return this.http.get(`https://dummyjson.com/c/68b8-5823-45c1-bfae`);
  }

  // GET recommended books
  getRecommendedBooks(): Observable<any> {
    return this.http.get(`${this.apiUrl}/recommended`);
  }

  // GET trending books
  getTrendingBooks(): Observable<any> {
    return this.http.get(`${this.apiUrl}/trending`);
  }

  // PUT update book
  updateBook(id: string, bookData: any): Observable<any> {
    // return this.http.put(`${this.apiUrl}/${id}`, bookData);
    return this.http.put(`https://dummyjson.com/c/d8ac-a228-4481-b4a7`, bookData);
  }

  // POST add a new book
  addBook(bookData: any): Observable<any> {
    // return this.http.post(`${this.apiUrl}`, bookData);
    return this.http.post(`https://dummyjson.com/c/99fe-ac11-4a5d-a462`, bookData);
  }

  // DELETE book by ID
  deleteBook(id: number | string): Observable<any> {
    // return this.http.delete(`${this.apiUrl}/${id}`);
    return this.http.delete(`https://dummyjson.com/c/dad4-58b5-4339-ae33`);
  }

  // BULK upload
  bulkUploadBooks(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post(`https://dummyjson.com/c/2316-ec07-4908-a71c`, formData);
  }



  // SEARCH methods
  searchBooksByTitle(title: string): Observable<any> {
    return this.http.get(
      // `${this.apiUrl}/search?title=${encodeURIComponent(title)}`
      `https://dummyjson.com/c/ee13-926c-484d-8fa9`
    );
  }

  searchBooksByAuthor(author: string): Observable<any> {
    return this.http.get(
      // `${this.apiUrl}/search?author=${encodeURIComponent(author)}`
      `https://dummyjson.com/c/ee13-926c-484d-8fa9`
    );
  }

  searchBooksByIsbn(isbn: string): Observable<any> {
    return this.http.get(
      // `${this.apiUrl}/search?isbn=${encodeURIComponent(isbn)}`
      `https://dummyjson.com/c/ee13-926c-484d-8fa9`
    );
  }

  // GET all categories
  getAllCategories(): Observable<any> {
    // return this.http.get(`${this.apiUrl}/get-all-categories`);
    return this.http.get(`https://dummyjson.com/c/eff1-4df4-47b8-9f10`);
  }

  // Get low stock books
  getLowStockBooks(threshold: number) {
  // return this.http.get<any[]>(`/api/books/low-stock?threshold=${threshold}`);
  return this.http.get<any[]>(`https://dummyjson.com/c/ee13-926c-484d-8fa9`);
}

// get review of a particular book
getReviewsUsingBookId(id: number | string) {
  // return this.http.get<any[]>(`reviews/get/${id}`);
  return this.http.get<any[]>(`https://dummyjson.com/c/0705-4faa-4ed0-abdb`);
}

  // POST add a new review
  addReview(reviewData: any, id: string): Observable<any> {
    // return this.http.post(`reviews/add/${id}`, bookData);
    return this.http.post(`https://dummyjson.com/c/0305-ed6c-4b8b-aaad`, reviewData);
  }

  // delete review for a particular book
  deleteReview(id: string): Observable<any> {
    // return this.http.post(`reviews/delete/${id}`);
    return this.http.delete(`https://dummyjson.com/c/c15e-106c-449b-8550`);
  }

}

// /api/books/search?title=book_name: 
// /api/categories/get-all : ✅
// /api/categories/get/name/category  ✅
// /api/books/search?category=category_name  ✅
// /search?sortBy=price&sortDir=asc  ✅
// /search?sortBy=publishDate&sortDir=desc  ✅
// /search?minPrice=price&maxPrice=asc  ✅
// /api/books/search?available=true   ✅

// placed,shift,cancelled,delivered,return,pending

// order id same , multiple product

// return orders list in admin section

// make changes in order history table in profile, after clicking the order id , another page open consisting that orders
// where I have the return option


// user statistics api/auth/admin/get/stats ✅
// admin: user count, admin count, customer count , recent user top 5 


