import { Component, OnInit, AfterViewInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-customer-behaviour',
  standalone: true,
  imports: [],
  templateUrl: './customer-behaviour.html',
  styleUrl: './customer-behaviour.css'
})
export class CustomerBehaviour implements OnInit, AfterViewInit {
  private apiUrl = 'https://dummyjson.com/c/046f-372c-4ddd-beb0';
  customerInsights: any[] = [];
  mostActiveCustomers: any[] = [];
  purchaseTrends: any = {};

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getCustomerBehavior();
  }

  ngAfterViewInit(): void {
    // Charts will be created after API loads
  }

  getCustomerBehavior() {
    this.http.get<any>(this.apiUrl).subscribe(data => {
      this.customerInsights = data.customerInsights;
      this.mostActiveCustomers = data.mostActiveCustomers;
      this.purchaseTrends = data.purchaseTrends;

      this.renderCustomerInsightsChart();
      this.renderMostActiveCustomersChart();
      this.renderPurchaseTrendsChart();
    });
  }

  renderCustomerInsightsChart() {
    const labels = this.customerInsights.map(c => c.name);
    const values = this.customerInsights.map(c => c.totalSpent);

    const config: ChartConfiguration = {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Total Spent',
            data: values,
            backgroundColor: 'rgba(54, 162, 235, 0.6)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Customer Insights - Total Spent'
          }
        }
      }
    };

    new Chart('customerInsightsChart', config);
  }

  renderMostActiveCustomersChart() {
    const labels = this.mostActiveCustomers.map(c => c.email);
    const values = this.mostActiveCustomers.map(c => c.totalOrders);

    const config: ChartConfiguration = {
      type: 'pie',
      data: {
        labels,
        datasets: [
          {
            label: 'Orders',
            data: values,
            backgroundColor: [
              'rgba(255, 99, 132, 0.6)',
              'rgba(255, 206, 86, 0.6)',
              'rgba(75, 192, 192, 0.6)'
            ]
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Most Active Customers - Orders'
          }
        }
      }
    };

    new Chart('mostActiveCustomersChart', config);
  }

  renderPurchaseTrendsChart() {
    const dailyLabels = this.purchaseTrends.dailyTrends.map((t: any) => t[0]);
    const dailyValues = this.purchaseTrends.dailyTrends.map((t: any) => t[2]);

    const weeklyLabels = this.purchaseTrends.weeklyTrends.map((t: any) => `Week ${t[0]}`);
    const weeklyValues = this.purchaseTrends.weeklyTrends.map((t: any) => t[2]);

    const monthlyLabels = this.purchaseTrends.monthlyTrends.map((t: any) => t[0]);
    const monthlyValues = this.purchaseTrends.monthlyTrends.map((t: any) => t[2]);

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: [...dailyLabels, ...weeklyLabels, ...monthlyLabels],
        datasets: [
          {
            label: 'Daily Spending',
            data: dailyValues,
            borderColor: 'rgba(255, 99, 132, 1)',
            backgroundColor: 'rgba(255, 99, 132, 0.3)',
            fill: false,
            tension: 0.1
          },
          {
            label: 'Weekly Spending',
            data: [...new Array(dailyLabels.length).fill(null), ...weeklyValues],
            borderColor: 'rgba(54, 162, 235, 1)',
            backgroundColor: 'rgba(54, 162, 235, 0.3)',
            fill: false,
            tension: 0.1
          },
          {
            label: 'Monthly Spending',
            data: [...new Array(dailyLabels.length + weeklyLabels.length).fill(null), ...monthlyValues],
            borderColor: 'rgba(75, 192, 192, 1)',
            backgroundColor: 'rgba(75, 192, 192, 0.3)',
            fill: false,
            tension: 0.1
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Purchase Trends (Daily, Weekly, Monthly)'
          }
        }
      }
    };

    new Chart('purchaseTrendsChart', config);
  }
}
