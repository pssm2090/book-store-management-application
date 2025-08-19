import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { OrderService } from '../../services/order';

Chart.register(...registerables);

@Component({
  selector: 'app-sales-report',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sales-report.html',
  styleUrls: ['./sales-report.css'],
})
export class SalesReportComponent implements AfterViewInit {
  private dailyChart: Chart<'line'> | null = null;
  private weeklyChart: Chart<'line'> | null = null;
  private monthlyChart: Chart<'line'> | null = null;

  constructor(private orderService: OrderService) {}

  ngAfterViewInit(): void {
    this.orderService.getSalesReport().subscribe((res) => {
      this.renderDailyChart(res.dailySales || {});
      this.renderWeeklyChart(res.weeklySales || {});
      this.renderMonthlyChart(res.monthlySales || {});
    });
  }

  private renderDailyChart(dailySales: any) {
    const labels = Object.keys(dailySales);
    const data = Object.values(dailySales) as number[];

    const config: ChartConfiguration<'line'> = {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            data,
            label: 'Daily Sales',
            borderColor: '#2563eb',
            backgroundColor: 'rgba(37,99,235,0.3)',
            fill: true,
            tension: 0.4
          }
        ]
      },
      options: { responsive: true }
    };

    const ctx = document.getElementById('dailyChart') as HTMLCanvasElement;
    if (ctx) {
      if (this.dailyChart) this.dailyChart.destroy();
      this.dailyChart = new Chart(ctx, config);
    }
  }

  private renderWeeklyChart(weeklySales: any) {
    const labels = Object.keys(weeklySales);
    const data = Object.values(weeklySales) as number[];

    const config: ChartConfiguration<'line'> = {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            data,
            label: 'Weekly Sales',
            borderColor: '#16a34a',
            backgroundColor: 'rgba(22,163,74,0.3)',
            fill: true,
            tension: 0.4
          }
        ]
      },
      options: { responsive: true }
    };

    const ctx = document.getElementById('weeklyChart') as HTMLCanvasElement;
    if (ctx) {
      if (this.weeklyChart) this.weeklyChart.destroy();
      this.weeklyChart = new Chart(ctx, config);
    }
  }

  private renderMonthlyChart(monthlySales: any) {
    const labels = Object.keys(monthlySales);
    const data = Object.values(monthlySales) as number[];

    const config: ChartConfiguration<'line'> = {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            data,
            label: 'Monthly Sales',
            borderColor: '#f59e0b',
            backgroundColor: 'rgba(245,158,11,0.3)',
            fill: true,
            tension: 0.4
          }
        ]
      },
      options: { responsive: true }
    };

    const ctx = document.getElementById('monthlyChart') as HTMLCanvasElement;
    if (ctx) {
      if (this.monthlyChart) this.monthlyChart.destroy();
      this.monthlyChart = new Chart(ctx, config);
    }
  }
}
