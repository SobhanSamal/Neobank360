import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { AnalyticsService } from '../../services/analytics.service';
 
@Component({
  selector: 'app-admin-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './admin-analytics.component.html',
  styleUrl: './admin-analytics.component.css'
})
export class AdminAnalyticsComponent implements OnInit {
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
  timeframe: '7d' | '30d' | 'YTD' = '30d';
  loading = true;
  error = '';
  transactionData: any;
  loanData: any;
  logData: any;
  transactionChart: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
  loanChart: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };
  errorRateChart: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };
  responseTrendChart: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
 
  constructor(private analytics: AnalyticsService) {}
 
  ngOnInit(): void { this.load(); }
 
  load(): void {
    this.loading = true;
    this.analytics.getAdminTransactions(this.timeframe).subscribe({
      next: res => {
        this.transactionData = res;
        this.transactionChart = {
          labels: res.dailyVolumes.map((x: any) => x.date),
          datasets: [
            { label: 'Daily volume', data: res.dailyVolumes.map((x: any) => x.totalAmount), borderColor: '#0f766e', backgroundColor: 'rgba(15,118,110,.2)' },
            { label: 'Count', data: res.dailyVolumes.map((x: any) => x.transactionCount), borderColor: '#b45309', backgroundColor: 'rgba(180,83,9,.2)' }
          ]
        };
        this.chart?.update();
      }
    });
    this.analytics.getAdminLoans().subscribe({
      next: res => {
        this.loanData = res;
        this.loanChart = {
          labels: Object.keys(res.loanDistribution),
          datasets: [{ label: 'Applications', data: Object.values(res.loanDistribution), backgroundColor: '#1d4ed8' }]
        };
        this.chart?.update();
      }
    });
    this.analytics.getSystemLogs({ page: 0, size: 50 }).subscribe({
      next: res => {
        this.logData = res;
        const errorBuckets = new Map<string, number>();
        const trendBuckets = new Map<string, number>();
        for (const log of res.content) {
          const key = new Date(log.eventTimestamp).toISOString().slice(0, 13);
          trendBuckets.set(key, (trendBuckets.get(key) || 0) + log.executionTimeMs);
          if (log.responseStatus >= 400) errorBuckets.set(key, (errorBuckets.get(key) || 0) + 1);
        }
        this.errorRateChart = { labels: Array.from(errorBuckets.keys()), datasets: [{ label: 'Errors', data: Array.from(errorBuckets.values()), backgroundColor: '#dc2626' }] };
        this.responseTrendChart = { labels: Array.from(trendBuckets.keys()), datasets: [{ label: 'Response time ms', data: Array.from(trendBuckets.values()), borderColor: '#7c3aed', backgroundColor: 'rgba(124,58,237,.2)' }] };
        this.loading = false;
      },
      error: () => { this.loading = false; this.error = 'Failed to load admin logs'; }
    });
  }
}
 