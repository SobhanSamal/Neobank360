import { CommonModule } from '@angular/common';
import { Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { AnalyticsService, UserSpendingAnalytics, UserWealthAnalytics } from '../../services/analytics.service';
import { AuthService } from '../../services/auth.service';
import { forkJoin } from 'rxjs';
 
@Component({
  selector: 'app-customer-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './customer-analytics.component.html',
  styleUrl: './customer-analytics.component.css'
})
export class CustomerAnalyticsComponent implements OnInit {
  @ViewChildren(BaseChartDirective) charts?: QueryList<BaseChartDirective>;
  loading = true;
  error = '';
  months = 6;
  spending?: UserSpendingAnalytics;
  wealth?: UserWealthAnalytics;
  spendingChart: ChartConfiguration<'doughnut'>['data'] = { labels: [], datasets: [] };
  budgetChart: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };
  netWorthChart: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
  rewardsChart: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
 
  constructor(private readonly analytics: AnalyticsService, private readonly auth: AuthService) {}
 
  ngOnInit(): void {
    this.load();
  }
 
  load(): void {
    const userId = this.auth.getUserId();
    if (!userId) {
      this.error = 'Please log in again to view your analytics.';
      this.loading = false;
      return;
    }
 
    this.loading = true;
    this.error = '';
 
    forkJoin({
      spending: this.analytics.getSpending(userId, this.months),
      wealth: this.analytics.getWealth(userId)
    }).subscribe({
      next: ({ spending, wealth }) => {
        this.spending = spending;
        this.wealth = wealth;
        this.spendingChart = {
          labels: spending.categorySpending.map(x => x.category),
          datasets: [{ data: spending.categorySpending.map(x => x.amount), backgroundColor: ['#0f766e', '#2563eb', '#b45309', '#7c3aed', '#dc2626', '#16a34a'] }]
        };
        this.budgetChart = {
          labels: spending.budgetVsActual.map(x => x.category),
          datasets: [
            { label: 'Budget', data: spending.budgetVsActual.map(x => x.budgetLimit), backgroundColor: 'rgba(37,99,235,.45)' },
            { label: 'Actual', data: spending.budgetVsActual.map(x => x.actualSpend), backgroundColor: 'rgba(220,38,38,.45)' }
          ]
        };
        this.netWorthChart = {
          labels: wealth.netWorthTimeline.map(x => x.month),
          datasets: [
            { label: 'Net worth', data: wealth.netWorthTimeline.map(x => x.netWorth), borderColor: '#0f766e', backgroundColor: 'rgba(15,118,110,.15)', tension: 0.35 },
            { label: 'Balances', data: wealth.netWorthTimeline.map(x => x.totalBalance), borderColor: '#2563eb', backgroundColor: 'rgba(37,99,235,.15)', tension: 0.35 }
          ]
        };
        this.rewardsChart = {
          labels: wealth.rewardAccrualHistory.map(x => x.month),
          datasets: [{ label: 'Reward points', data: wealth.rewardAccrualHistory.map(x => x.rewardPoints), borderColor: '#b45309', backgroundColor: 'rgba(180,83,9,.2)', tension: 0.35 }]
        };
        this.loading = false;
        this.refreshCharts();
      },
      error: () => {
        this.error = 'Failed to load customer analytics.';
        this.loading = false;
      }
    });
  }
 
  private refreshCharts(): void {
    this.charts?.forEach(chart => chart.update());
  }
}
 