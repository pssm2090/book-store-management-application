import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../services/user';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stats.html',
  styleUrls: ['./stats.css']
})
export class StatsComponent implements OnInit {
  loading = true;
  stats: any = null;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.fetchStats();
  }

  fetchStats() {
    this.loading = true;
    this.userService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;

        // ✅ Success toast
        Swal.fire({
          icon: 'success',
          title: 'Stats Loaded!',
          toast: true,
          position: 'top-end',
          timer: 2000,
          showConfirmButton: false
        });
      },
      error: (err) => {
        this.loading = false;
        
        // ❌ Error popup
        Swal.fire({
          icon: 'error',
          title: 'Failed to load stats',
          text: err?.message || 'Something went wrong!'
        });
      }
    });
  }
}
