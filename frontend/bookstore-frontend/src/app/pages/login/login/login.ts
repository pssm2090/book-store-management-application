import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Auth } from '../../../services/auth';
import Swal from 'sweetalert2';

@Component({
  imports: [CommonModule, FormsModule, RouterModule],
  selector: 'app-login',
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  email: string = '';
  password: string = '';

  constructor(
    private router: Router,
    private authService: Auth
  ) {}

  handleSubmit(event: Event) {
    event.preventDefault();

    this.authService.login({ email: this.email, password: this.password })
      .subscribe({
        next: (res) => {
          // ✅ Store tokens in localStorage
          localStorage.setItem('accessToken', res.accessToken);
          localStorage.setItem('refreshToken', res.refreshToken);

          // (Optional) store user info
          localStorage.setItem('user', JSON.stringify({
            name: res.name,
            email: res.email,
            role: res.role
          }));

          Swal.fire({
            icon: 'success',
            title: 'Login Successful',
            text: `Welcome back, ${res.name}!`
          });

          // ✅ Redirect to home
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Login failed', err);
          Swal.fire({
            icon: 'error',
            title: 'Login Failed',
            text: 'Invalid email or password. Please try again.'
          });
        }
      });
  }
}
