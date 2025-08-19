import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api'; 
  private getHeaders() {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }
  
  private cartItems = new BehaviorSubject<any[]>([]);
  cartItems$ = this.cartItems.asObservable();

  constructor(private http: HttpClient) {}

  getCart(): Observable<any> {
    return this.http.get(`${this.apiUrl}/cart/get`,{
      headers: this.getHeaders()
    });
    // return this.http.get(`https://dummyjson.com/c/3b79-4674-48cd-8b44`);
  }

  addToCart(bookId: string, quantity: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/cart/add`, { bookId, quantity },{
      headers: this.getHeaders()
    });
    // return this.http.post("https://dummyjson.com/c/64f3-f1ad-4174-b01f",{bookId,quantity});
  }

  removeFromCart(itemId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/cart/remove/${itemId}`,{
      headers: this.getHeaders()
    });
    // return this.http.delete(`https://dummyjson.com/c/797b-216e-47af-a18d`);
  }

 clearCart(): Observable<any> {
  return this.http.delete(`${this.apiUrl}/cart/clear`, {
    headers: this.getHeaders(),
    observe: 'response',
    responseType: 'text'  // <-- prevents JSON parse error
  });
}


 updateCartState(payload: { cartItemId: number; quantity: number }) {
  return this.http.put(`${this.apiUrl}/cart/update`, payload, {
    headers: this.getHeaders().set('Content-Type', 'application/json')
  });
}

  // return this.http.put("https://dummyjson.com/c/592d-aa67-4eca-9db0", payload);
}


