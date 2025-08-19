import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api'; 

  private getHeaders() {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }
  

  constructor(private http: HttpClient) {}

  placeOrder(orderData: any): Observable<any> {
    return this.http.post(this.apiUrl, orderData);
  }

  getOrders(): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders/get/my-orders`,{
      headers: this.getHeaders()
    });
    // return this.http.get("https://dummyjson.com/c/1069-a7ee-47ff-ac11");
  }

  // get all orders for admin
  
  getOrdersAdmin(): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders/get-all`,{
      headers: this.getHeaders()
    });
  }

  //  Update order status
  updateOrderStatus(orderId: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/orders/${orderId}/status`, { status },{
      headers: this.getHeaders()
    });
    // return this.http.put(`https://dummyjson.com/c/3bb8-537c-458e-9630`, { status });
    // orders/2/status
  }

  // sales report
  getSalesReport() : Observable<any>{
    return this.http.get(`${this.apiUrl}/reports/sales`,{
      headers: this.getHeaders()
    });
    // return this.http.get("reports/sales");
    ///reports/sales
  }

  // inventory reporting
  getInventoryReporting(): Observable<any>{
    return this.http.get(`${this.apiUrl}/reports/inventory`,{
      headers: this.getHeaders()
    });
  }

  // customer trends
  getCustomerBehavior(): Observable<any>{
    return this.http.get(`${this.apiUrl}/reports/customer-behavior`,{
      headers: this.getHeaders()
    });
  }

   // revenue trends
  getRevenueTracking(): Observable<any>{
    return this.http.get(`${this.apiUrl}/reports/revenue`,{
      headers: this.getHeaders()
    });
  }
  
}
