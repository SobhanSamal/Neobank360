import { CommonModule } from '@angular/common';
import { Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import {
  AnalyticsService,
  SystemAuditLog,
  SystemAuditLogsPage
} from '../../services/analytics.service';

@Component({
  selector: 'app-system-logs',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './system-logs.component.html',
  styleUrl: './system-logs.component.css'
})
export class SystemLogsComponent implements OnInit {

  @ViewChildren(BaseChartDirective)
  charts?: QueryList<BaseChartDirective>;

  // ✅ UI STATE
  loading = true;
  error = '';
  searchTerm = '';
  from = '';
  to = '';
  status = '';

  // ✅ PAGINATION
  page = 0;
  size = 50;

  // ✅ DATA
  logsPage: SystemAuditLogsPage | null = null;
  filteredLogs: SystemAuditLog[] = [];

  // ✅ CHART DATA
  errorRateChart: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: []
  };

  responseTrendChart: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };

  constructor(private readonly analytics: AnalyticsService) {}

  // ✅ INIT
  ngOnInit(): void {
    this.load();
  }

  // ✅ REFRESH CHART
  private refreshCharts(): void {
    this.charts?.forEach(chart => chart.update());
  }

  // ✅ MAIN LOAD FUNCTION
  load(): void {
    this.loading = true;
    this.error = '';

    const params: {
      from?: string;
      to?: string;
      status?: number;
      page: number;
      size: number;
    } = {
      page: this.page,
      size: this.size
    };

    if (this.from) params.from = `${this.from}T00:00:00`;
    if (this.to) params.to = `${this.to}T23:59:59`;
    if (this.status) params.status = Number(this.status);

    this.analytics.getSystemLogs(params).subscribe({
      next: res => {
        this.logsPage = res;

        // ✅ APPLY SEARCH FILTER
        this.applySearch();

        // ✅ BUCKETS
        const errorBuckets = new Map<
          string,
          { errors: number; total: number }
        >();

        const trendBuckets = new Map<
          string,
          { sum: number; count: number }
        >();

        // ✅ PROCESS LOGS
        for (const log of res.content) {
          const key = new Date(log.eventTimestamp)
            .toISOString()
            .slice(0, 13); // hourly

          // ✅ ERROR RATE
          const errBucket =
            errorBuckets.get(key) ||
            { errors: 0, total: 0 };

          errBucket.total++;

          if (log.responseStatus >= 400) {
            errBucket.errors++;
          }

          errorBuckets.set(key, errBucket);

          // ✅ RESPONSE TIME
          const trend =
            trendBuckets.get(key) ||
            { sum: 0, count: 0 };

          trend.sum += log.executionTimeMs;
          trend.count++;

          trendBuckets.set(key, trend);
        }

        // ✅ SORT KEYS
        const sortedErrorKeys =
          Array.from(errorBuckets.keys()).sort();

        const sortedTrendKeys =
          Array.from(trendBuckets.keys()).sort();

        // ✅ ERROR % DATA
        const errorLabels: string[] = [];
        const errorData: number[] = [];

        sortedErrorKeys.forEach(key => {
          const val = errorBuckets.get(key)!;

          const rate =
            val.total === 0
              ? 0
              : (val.errors / val.total) * 100;

          errorLabels.push(key);
          errorData.push(Number(rate.toFixed(2)));
        });

        // ✅ RESPONSE TIME DATA (AVG)
        const trendLabels: string[] = [];
        const trendData: number[] = [];

        sortedTrendKeys.forEach(key => {
          const val = trendBuckets.get(key)!;

          const avg =
            val.count === 0
              ? 0
              : val.sum / val.count;

          trendLabels.push(key);
          trendData.push(Number(avg.toFixed(2)));
        });

        // ✅ FINAL CHART CONFIG

        this.errorRateChart = {
          labels: errorLabels,
          datasets: [
            {
              label: 'Error %',
              data: errorData,
              backgroundColor: '#dc2626'
            }
          ]
        };

        this.responseTrendChart = {
          labels: trendLabels,
          datasets: [
            {
              label: 'Avg Response Time (ms)',
              data: trendData,
              borderColor: '#7c3aed',
              backgroundColor: 'rgba(124,58,237,0.2)',
              tension: 0.35
            }
          ]
        };

        this.loading = false;

        // ✅ UPDATE CHART
        this.refreshCharts();
      },

      error: () => {
        this.loading = false;
        this.error = 'Failed to load system logs.';
      }
    });
  }

  // ✅ SEARCH FILTER
  applySearch(): void {
    const term = this.searchTerm.trim().toLowerCase();
    const logs = this.logsPage?.content ?? [];

    this.filteredLogs = term
      ? logs.filter(log =>
          [
            log.endpoint,
            log.httpMethod,
            String(log.responseStatus),
            log.errorMessage ?? ''
          ]
            .join(' ')
            .toLowerCase()
            .includes(term)
        )
      : logs;
  }

  // ✅ RESET FILTERS
  clearFilters(): void {
    this.searchTerm = '';
    this.from = '';
    this.to = '';
    this.status = '';
    this.page = 0;
    this.load();
  }
}