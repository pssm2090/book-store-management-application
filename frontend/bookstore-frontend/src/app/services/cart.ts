import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'https://api.example.com/cart'; // change to your API
  private cartItems = new BehaviorSubject<any[]>([]);
  cartItems$ = this.cartItems.asObservable();

  constructor(private http: HttpClient) {}

  getCart(): Observable<any> {
    // return this.http.get(this.apiUrl);
    return this.http.get(`https://dummyjson.com/c/3b79-4674-48cd-8b44`);
  }

  addToCart(bookId: string, quantity: number): Observable<any> {
    // return this.http.post(this.apiUrl, { bookId, quantity });
    return this.http.post("https://dummyjson.com/c/64f3-f1ad-4174-b01f",{bookId,quantity});
  }

  removeFromCart(itemId: number): Observable<any> {
    // return this.http.delete(`${this.apiUrl}/${itemId}`);
    return this.http.delete(`https://dummyjson.com/c/797b-216e-47af-a18d`);
  }

  clearCart(): Observable<any> {
    // return this.http.delete(this.apiUrl);
    return this.http.delete("https://dummyjson.com/c/e6f2-a7a1-4be0-94ce");
  }

 updateCartState(payload: { cartItemId: number; quantity: number }) {
  // return this.http.put("http://localhost:8080/api/cart/update", payload);
  return this.http.put("https://dummyjson.com/c/592d-aa67-4eca-9db0", payload);
}

}
