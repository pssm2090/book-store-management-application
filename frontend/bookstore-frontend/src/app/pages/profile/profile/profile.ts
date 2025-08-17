import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { UserService } from '../../../services/user';
import { OrderService } from '../../../services/order';
import { Auth } from '../../../services/auth'; // ✅ import Auth service
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.html',
  styleUrls: ['./profile.css'],
  imports: [CommonModule, FormsModule],
})
export class Profile implements OnInit {
  user: any = null;

  name: string = '';
  email: string = '';
  role: string = '';
  avatar: string = '';
  password: string = '';
  oldPassword: string = '';

  orders: any[] = [];

  get isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  constructor(
    private userService: UserService,
    private orderService: OrderService,
    private authService: Auth, // ✅ inject Auth
    private router: Router // ✅ inject Router
  ) {}

  ngOnInit() {
    window.scrollTo(0, 0);
    this.loadProfile();
    this.loadOrders();
  }

  loadProfile() {
    this.userService.getUserProfile().subscribe({
      next: (data) => {
        this.user = data;
        this.name = data.name;
        this.email = data.email;
        this.role = data.role;

        this.avatar = `https://ui-avatars.com/api/?name=${encodeURIComponent(
          data.name
        )}&background=random`;
      },
      error: (err) => {
        Swal.fire('Error!', 'Failed to load profile', 'error');
        console.error('Profile load error:', err);
      },
    });
  }

  loadOrders() {
    this.orderService.getOrders().subscribe({
      next: (data) => {
        this.orders = data;
      },
      error: (err) => {
        Swal.fire('Error!', 'Failed to load orders', 'error');
        console.error('Orders load error:', err);
      },
    });
  }

  handleSave() {
    if (!this.oldPassword.trim()) {
      Swal.fire(
        'Error!',
        'Old password is required to update profile.',
        'error'
      );
      return;
    }

    const updatedData: any = { oldPassword: this.oldPassword };

    if (this.name !== this.user.name) {
      updatedData.name = this.name;
    }

    if (this.password.trim()) {
      updatedData.newPassword = this.password;
    }

    Swal.fire({
      icon: 'info',
      title: 'Saving...',
      allowOutsideClick: false,
      didOpen: () => Swal.showLoading(),
    });

    this.userService.updateUserProfile(updatedData).subscribe({
      next: (res) => {
        Swal.fire('Success!', 'Profile updated successfully', 'success');
        this.password = '';
        this.oldPassword = '';
        this.user.name = this.name;
      },
      error: (err) => {
        Swal.fire('Error!', 'Failed to update profile', 'error');
        console.error('Profile update error:', err);
      },
    });
  }

  // ✅ Logout method
  handleLogout() {
    Swal.fire({
      icon: 'warning',
      title: 'Are you sure?',
      text: 'You will be logged out of your account.',
      showCancelButton: true,
      confirmButtonText: 'Logout',
      cancelButtonText: 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.authService.logout(); // clear tokens
        this.router.navigate(['/login']); // redirect to login page
        Swal.fire('Logged out!', 'You have been logged out.', 'success');
      }
    });
  }
}
