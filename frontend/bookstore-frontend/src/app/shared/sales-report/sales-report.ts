import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  Chart,
  ChartConfiguration,
  ChartType,
  registerables
} from 'chart.js';

Chart.register(...registerables); // very important! register chart.js modules

@Component({
  selector: 'app-sales-report',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sales-report.html',
  styleUrls: ['./sales-report.css']
})
export class SalesReportComponent implements AfterViewInit {
  private chart: Chart<'line'> | null = null;

  constructor() {}

  ngAfterViewInit(): void {
    this.loadSalesData();
  }

  loadSalesData() {
    const response = {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May'],
      data: [120, 200, 150, 300, 250]
    };

    const config: ChartConfiguration<'line'> = {
      type: 'line',
      data: {
        labels: response.labels,
        datasets: [
          {
            data: response.data,
            label: 'Sales',
            borderColor: '#2563eb',
            backgroundColor: 'rgba(37,99,235,0.3)',
            fill: true,
            tension: 0.4
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true }
        }
      }
    };

    const ctx = document.getElementById('salesChart') as HTMLCanvasElement;
    if (ctx) {
      if (this.chart) this.chart.destroy(); // avoid duplicates
      this.chart = new Chart(ctx, config);
    }
  }
}
