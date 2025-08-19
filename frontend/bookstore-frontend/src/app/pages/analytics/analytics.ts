import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, RouterModule], // so routerLink works
  templateUrl: './analytics.html',
  styleUrls: ['./analytics.css'] // must be plural
})
export class Analytics {}
