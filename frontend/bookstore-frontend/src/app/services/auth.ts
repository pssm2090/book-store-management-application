import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private apiUrl = 'https://api.example.com/api/auth'; // change to your API
  private refreshUrl = 'http://localhost:8080/api/auth/refresh-token';

  private authState = new BehaviorSubject<boolean>(this.hasTokens());
  isAuthenticated$ = this.authState.asObservable();

  constructor(private http: HttpClient) {}

  // ===== API Calls =====
  login(credentials: { email: string; password: string }): Observable<any> {
    // return this.http.post(`${this.apiUrl}/login`, credentials);
    return this.http.post(`https://dummyjson.com/c/cfac-100a-4f6f-9325`, credentials);
  }

  register(data: any): Observable<any> {
    // return this.http.post(`${this.apiUrl}/register`, data);
    return this.http.post(`https://dummyjson.com/c/1a06-def2-40c3-a22f`, data);
  }

  refreshToken(): Observable<any> {
    const refresh = localStorage.getItem('refreshToken');
    if (!refresh) return of(null);

    return this.http.post(this.refreshUrl, { refreshToken: refresh }).pipe(
      tap((res: any) => {
        localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
      }),
      catchError(() => {
        this.logout();
        return of(null);
      })
    );
  }

  // ===== Token Handling =====
  saveTokens(accessToken: string, refreshToken: string) {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    this.authState.next(true);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.authState.next(false);
  }

  getUserRole(): string | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user).role : null;
  }

  getUserName(): string | null {
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  return user ? user.name : null;
}


  isLoggedIn(): boolean {
    return !!localStorage.getItem('accessToken');
  }

  private hasTokens(): boolean {
    return !!localStorage.getItem('accessToken') && !!localStorage.getItem('refreshToken');
  }
}
