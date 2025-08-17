import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'https://api.example.com/api'; // change to your API

  constructor(private http: HttpClient) {}

  getProfile(): Observable<any> {
    return this.http.get(`${this.apiUrl}/profile`);
  }

  updateProfile(profileData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/profile`, profileData);
  }

  // Fetch user stats
  getStats(): Observable<any> {
    // return this.http.get(`${this.apiUrl}/stats`);
    return this.http.get(`https://dummyjson.com/c/5cbd-1669-483a-a8ef`);
  }

  // Fetch all users in admin section
  getAllUsers(): Observable<any> {
    // return this.http.get(`${this.apiUrl}/auth/admin/get/users`);
    return this.http.get(`https://dummyjson.com/c/3fba-8932-4a12-9b3a`);
  }

  // Fetch user profile
  getUserProfile(): Observable<any> {
    // return this.http.get(`${this.apiUrl}/auth/get/profile`);
    return this.http.get(`https://dummyjson.com/c/785e-543a-4ffd-b3f2`);
  }

  // update user profile
  updateUserProfile(profileData: any): Observable<any> {
    // return this.http.get(`${this.apiUrl}/auth/update/profile`);
    return this.http.put(`https://dummyjson.com/c/5c74-b87f-4eb9-9138`,profileData);
  }
}
