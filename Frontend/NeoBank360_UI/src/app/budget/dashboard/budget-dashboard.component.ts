import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import {
  BudgetApiService,
  BudgetSummaryData
} from '../../services/budget-api.service';

import { AuthService } from '../../services/auth.service';

import { ChartConfiguration, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

@Component({
  selector: 'app-budget-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './budget-dashboard.component.html',
  styleUrl: './budget-dashboard.component.css'
})
export class BudgetDashboardComponent implements OnInit {

  summary: BudgetSummaryData[] = [];

  /* ✅ MONTH FILTER */
  months: string[] = [];
  selectedMonth = '';

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  barChartType: ChartType = 'bar';

  barChartData: ChartConfiguration['data'] = {
    labels: [],
    datasets: [
      {
        label: 'Budget',
        data: [],
        backgroundColor: '#10b981'
      },
      {
        label: 'Spent',
        data: [],
        backgroundColor: '#ef4444'
      }
    ]
  };

  barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    scales: {
      y: { beginAtZero: true }
    }
  };

  pieChartType: ChartType = 'pie';

  pieChartData: ChartConfiguration['data'] = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: [
          '#10b981',
          '#3b82f6',
          '#f59e0b',
          '#ef4444',
          '#8b5cf6'
        ]
      }
    ]
  };

  constructor(
    private budgetApi: BudgetApiService,
    private authService: AuthService,
    private router: Router
  ) {}

  /* ✅ INIT */
  ngOnInit(): void {

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.generateMonths();

    this.selectedMonth = this.months[0]; // ✅ latest month

    this.loadSummary(this.selectedMonth);
  }

  /* ✅ GENERATE MONTHS (till Dec 2026) */
  generateMonths(): void {

  this.months = [];

  const today = new Date();

  const startYear = today.getFullYear();     // ✅ current year
  const startMonth = today.getMonth();       // ✅ current month

  const endYear = 2026;                      // ✅ limit
  const endMonth = 11;                       // ✅ December

  for (let year = startYear; year <= endYear; year++) {

    for (let month = 0; month <= 11; month++) {

      // ✅ Start from current month only
      if (year === startYear && month < startMonth) continue;

      // ✅ Stop at Dec 2026
      if (year === endYear && month > endMonth) continue;

      const d = new Date(year, month, 1);

      this.months.push(d.toISOString().slice(0, 7));
    }
  }
}
  /* ✅ CHANGE MONTH */
  onMonthChange(): void {
    this.loadSummary(this.selectedMonth);
  }

  /* ✅ LOAD */
  loadSummary(month: string): void {

    this.budgetApi.getSummary(month).subscribe(data => {

      this.summary = data || [];

      // BAR
      this.barChartData.labels = data.map(i => i.category);
      this.barChartData.datasets[0].data = data.map(i => Number(i.limitAmount));
      this.barChartData.datasets[1].data = data.map(i => Number(i.spentAmount));

      // PIE
      this.pieChartData.labels = data.map(i => i.category);
      this.pieChartData.datasets[0].data = data.map(i => Number(i.spentAmount));

      this.chart?.update();
    });
  }

  /* ✅ STATS */
  totalBudget(): number {
    return this.summary.reduce((s, i) => s + Number(i.limitAmount), 0);
  }

  totalSpent(): number {
    return this.summary.reduce((s, i) => s + Number(i.spentAmount), 0);
  }

  totalRemaining(): number {
    return this.summary.reduce((s, i) => s + Number(i.remainingAmount), 0);
  }

  highestCategory(): string {
    if (!this.summary.length) return 'N/A';

    return [...this.summary]
      .sort((a, b) => Number(b.spentAmount) - Number(a.spentAmount))[0]
      .category;
  }

  goToCreate(): void {
    this.router.navigate(['/budget/create']);
  }
}