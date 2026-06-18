import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { AuthService } from '../services/auth.service';
import { FinancialInsights, InsightsService } from '../services/insights.service';

@Component({
  selector: 'app-insights-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './insights-dashboard.component.html',
  styleUrl: './insights-dashboard.component.css'
})
export class InsightsDashboardComponent implements OnInit {

  loading = true;
  error = '';
  insights: FinancialInsights | null = null;

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  chartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: []
  };

  chartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom' }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => this.formatCompact(Number(value))
        }
      }
    }
  };

  constructor(
    private insightsService: InsightsService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadInsights();
  }

  /* ✅ LOAD DATA */
  loadInsights(): void {

    const userId = this.authService.getUserId();

    if (!userId) {
      this.error = 'Invalid user';
      this.loading = false;
      return;
    }

    this.loading = true;
    this.error = '';

    this.insightsService.getInsights(userId).subscribe({

      next: (data) => {

        console.log('INSIGHTS DATA:', data); // ✅ debug

        this.insights = data;

        this.updateChart(data);

        this.loading = false;
      },

      error: () => {
        this.error = 'Failed to load insights';
        this.loading = false;
      }
    });
  }

  /* ✅ ✅ ✅ FIXED CHART UPDATE */
  updateChart(data: FinancialInsights) {

    if (!data.trendSummary || data.trendSummary.length === 0) {
      this.clearChart();
      return;
    }

    // ✅ recreate object (IMPORTANT)
    this.chartData = {
      labels: data.trendSummary.map(e => e.month),
      datasets: [
        {
          label: 'Income',
          data: data.trendSummary.map(e => Number(e.totalIncome)),
          backgroundColor: '#2e7d32',
          borderRadius: 4
        },
        {
          label: 'Expense',
          data: data.trendSummary.map(e => Number(e.totalExpense)),
          backgroundColor: '#c62828',
          borderRadius: 4
        }
      ]
    };

    this.chart?.update();
  }

  /* ✅ CLEAR */
  clearChart() {
    this.chartData = { labels: [], datasets: [] };
    this.chart?.update();
  }

  /* ✅ FORMAT */
  formatCurrency(value: number | undefined): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(Number(value || 0));
  }

  private formatCompact(value: number): string {
    return new Intl.NumberFormat('en-IN', {
      notation: 'compact',
      maximumFractionDigits: 1
    }).format(value);
  }
}