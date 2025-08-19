import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { OrderService } from '../../services/order';

Chart.register(...registerables);

@Component({
  selector: 'app-revenue-tracking',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './revenue-tracking.html',
  styleUrls: ['./revenue-tracking.css']
})
export class RevenueTracking implements OnInit {
  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadRevenueTracking();
  }

  loadRevenueTracking(): void {
    this.orderService.getRevenueTracking().subscribe((data) => {
      // ✅ Cast object values to number[]
      const categoryLabels = Object.keys(data.revenueByCategory);
      const categoryValues = Object.values(data.revenueByCategory) as number[];

      const bookLabels = Object.keys(data.revenuePerBook);
      const bookValues = Object.values(data.revenuePerBook) as number[];

      const timeLabels = Object.keys(data.revenueOverTime);
      const timeValues = Object.values(data.revenueOverTime).map((t: any) =>
        Object.values(t)[0]
      ) as number[];

      // Chart 1: Revenue by Category (Bar Chart)
      new Chart('revenueByCategoryChart', {
        type: 'bar',
        data: {
          labels: categoryLabels,
          datasets: [
            {
              label: 'Revenue',
              data: categoryValues, // ✅ now number[]
              backgroundColor: 'rgba(54, 162, 235, 0.6)',
            },
          ],
        },
      });

      // Chart 2: Revenue per Book (Pie Chart)
      new Chart('revenuePerBookChart', {
        type: 'pie',
        data: {
          labels: bookLabels,
          datasets: [
            {
              label: 'Revenue per Book',
              data: bookValues, // ✅ now number[]
              backgroundColor: [
                'rgba(255, 99, 132, 0.6)',
                'rgba(54, 162, 235, 0.6)',
                'rgba(255, 206, 86, 0.6)',
                'rgba(75, 192, 192, 0.6)',
                'rgba(153, 102, 255, 0.6)',
              ],
            },
          ],
        },
      });

      // Chart 3: Revenue Over Time (Line Chart)
      new Chart('revenueOverTimeChart', {
        type: 'line',
        data: {
          labels: timeLabels,
          datasets: [
            {
              label: 'Revenue Over Time',
              data: timeValues, // ✅ now number[]
              borderColor: 'rgba(75, 192, 192, 1)',
              fill: false,
            },
          ],
        },
      });
    });
  }
}
