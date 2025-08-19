import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { OrderService } from '../../services/order';

Chart.register(...registerables);

@Component({
  selector: 'app-inventory-report',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inventory-report.html',
  styleUrls: ['./inventory-report.css']
})
export class InventoryReport implements OnInit, AfterViewInit {
  constructor(private http: HttpClient,private orderService: OrderService) {}

  private bestSellersChart!: Chart;
  private outOfStockChart!: Chart;
  private lowStockChart!: Chart;

  private inventoryData: any;

  ngOnInit(): void {
     this.orderService.getInventoryReporting().subscribe((res) => {
      this.inventoryData = res;
      this.renderCharts();
    });
  }

  ngAfterViewInit(): void {
    if (this.inventoryData) {
      this.renderCharts();
    }
  }

  private renderCharts(): void {
    if (!this.inventoryData) return;

    // Destroy previous charts to avoid duplicates
    this.bestSellersChart?.destroy();
    this.outOfStockChart?.destroy();
    this.lowStockChart?.destroy();

    // Bestsellers - Bar Chart
    this.bestSellersChart = new Chart('bestSellersChart', {
      type: 'bar',
      data: {
        labels: this.inventoryData.bestsellers.map((b: any) => b.title),
        datasets: [{
          label: 'Stock Quantity',
          data: this.inventoryData.bestsellers.map((b: any) => b.stockQuantity),
          backgroundColor: '#42A5F5'
        }]
      },
      options: { responsive: true, plugins: { legend: { display: false } } }
    } as ChartConfiguration);

    // Out of Stock - Pie Chart
    this.outOfStockChart = new Chart('outOfStockChart', {
      type: 'pie',
      data: {
        labels: this.inventoryData.outOfStock.map((b: any) => b.title),
        datasets: [{
          label: 'Out of Stock',
          data: this.inventoryData.outOfStock.map(() => 1),
          backgroundColor: ['#EF5350', '#AB47BC', '#FF7043', '#66BB6A']
        }]
      },
      options: { responsive: true }
    } as ChartConfiguration);

    // Low Stock - Horizontal Bar
    this.lowStockChart = new Chart('lowStockChart', {
      type: 'bar',
      data: {
        labels: this.inventoryData.lowStock.map((b: any) => b.title),
        datasets: [{
          label: 'Low Stock Qty',
          data: this.inventoryData.lowStock.map((b: any) => b.stockQuantity),
          backgroundColor: '#FFCA28'
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        plugins: { legend: { display: false } }
      }
    } as ChartConfiguration);
  }
}
