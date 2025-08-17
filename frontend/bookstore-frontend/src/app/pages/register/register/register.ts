import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import Swal from 'sweetalert2';
import { Auth } from '../../../services/auth';

@Component({
  imports: [FormsModule, CommonModule, RouterModule],
  selector: 'app-register',
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class Register {
  formData = {
    name: '',
    email: '',
    password: '',
    role: 'CUSTOMER'
  };

  loading = false;

  constructor(private router: Router, private authService: Auth) {}

  handleSubmit(event: Event) {
    event.preventDefault();

    // ✅ Client-side validation
    if (!this.formData.name || !this.formData.email || !this.formData.password || !this.formData.role) {
      Swal.fire('⚠️ Warning', 'All fields are required.', 'warning');
      return;
    }

    this.loading = true;

    this.authService.register(this.formData).subscribe({
      next: (res) => {
        this.authService.saveTokens(res.accessToken, res.refreshToken);
        localStorage.setItem('user', JSON.stringify({
            name: res.name,
            email: res.email,
            role: res.role
          }));

        Swal.fire('✅ Success', res.message || 'Registration successful', 'success');

        this.router.navigate(['/']);
        this.loading = false;
      },
      error: (err) => {
        console.error('Registration failed:', err);
        Swal.fire('❌ Error', 'Failed to register. Please try again.', 'error');
        this.loading = false;
      }
    });
  }
}
