import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { RewardApiService, RewardHistoryData } from '../../services/reward-api.service';
 
@Component({
  selector: 'app-reward-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './reward-history.component.html',
  styleUrl: './reward-history.component.css',
})
export class RewardHistoryComponent implements OnInit {
  loading = false;
  error = '';
  history: RewardHistoryData[] = [];
 
  constructor(private readonly rewardApi: RewardApiService) {}
 
  ngOnInit(): void {
    this.loadHistory();
  }
 
  loadHistory(): void {
    this.loading = true;
    this.error = '';
 
    this.rewardApi.getHistory().subscribe({
      next: (history) => {
        this.history = history;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message || 'Failed to load reward history';
        this.loading = false;
      },
    });
  }
 
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(Number(amount || 0));
  }
 
  formatDate(value?: string): string {
    if (!value) {
      return '-';
    }
 
    return new Intl.DateTimeFormat('en-IN', {
      dateStyle: 'medium',
      timeStyle: 'short',
    }).format(new Date(value));
  }
}
 