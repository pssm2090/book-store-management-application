import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'https://api.example.com/orders'; // change to your API

  constructor(private http: HttpClient) {}

  placeOrder(orderData: any): Observable<any> {
    return this.http.post(this.apiUrl, orderData);
  }

  getOrders(): Observable<any> {
    return this.http.get("https://dummyjson.com/c/1069-a7ee-47ff-ac11");
  }

  // get all orders for admin
  
  getOrdersAdmin(): Observable<any> {
    return this.http.get("https://dummyjson.com/c/763a-8097-4bb8-95be");
  }

  //  Update order status
  updateOrderStatus(orderId: number, status: string): Observable<any> {
    // return this.http.put(`${this.apiUrl}/${orderId}/status`, { status });
    return this.http.put(`https://dummyjson.com/c/3bb8-537c-458e-9630`, { status });
  }
  
}
