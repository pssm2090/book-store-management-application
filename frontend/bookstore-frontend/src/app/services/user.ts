import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/auth'; // change to your API

  constructor(private http: HttpClient) {}

  private getHeaders() {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  // Fetch user stats
  getStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/admin/get/stats`, {
      headers: this.getHeaders()
    });
    // return this.http.get(`https://dummyjson.com/c/5cbd-1669-483a-a8ef`);
  }

  // Fetch all users in admin section
  getAllUsers(): Observable<any> {
    return this.http.get(`${this.apiUrl}/admin/get/users`, {
      headers: this.getHeaders()
    });
    // return this.http.get(`https://dummyjson.com/c/3fba-8932-4a12-9b3a`);
  }

  // Fetch user profile
  getUserProfile(): Observable<any> {
    // return this.http.get(`${this.apiUrl}/auth/get/profile`);
    return this.http.get(`${this.apiUrl}/get/profile`, {
      headers: this.getHeaders()
    });
  }

  // update user profile
  updateUserProfile(profileData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/profile`, profileData, {
      headers: this.getHeaders()
    });
        // return this.http.put(`https://dummyjson.com/c/5c74-b87f-4eb9-9138`,profileData);

  }
}
