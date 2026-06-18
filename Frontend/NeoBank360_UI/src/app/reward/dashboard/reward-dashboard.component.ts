import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
 
import {  RewardApiService,  RewardData,} from '../../services/reward-api.service';
 
@Component({
  selector: 'app-reward-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './reward-dashboard.component.html',
  styleUrls: ['./reward-dashboard.component.css'], // ✅ FIXED (styleUrl → styleUrls)
})
export class RewardDashboardComponent implements OnInit {
 
  loading = false;
  error = '';
  reward: RewardData | null = null;
 
  constructor(
    private readonly rewardApi: RewardApiService,
    private readonly cdr: ChangeDetectorRef
  ) {}
 
  ngOnInit(): void {
    this.loadRewards();
 
    // ✅ LIVE UPDATE
    this.rewardApi.balance$.subscribe((balance: number | null) => {
      if (this.reward && balance !== null) {
        this.reward.pointsBalance = balance;
        this.cdr.detectChanges();
      }
    });
  }
 
  loadRewards(): void {
    this.loading = true;
    this.error = '';
 
    this.rewardApi.getBalance().subscribe({
      next: (reward: RewardData) => {
        this.reward = reward;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err: Error) => {
        this.loading = false;
        this.error = err.message || 'Failed to load rewards';
        this.cdr.detectChanges();
      },
    });
  }
 
  formatUpdated(value?: string): string {
    if (!value) {
      return 'Not updated yet';
    }
 
    return new Intl.DateTimeFormat('en-IN', {
      dateStyle: 'medium',
      timeStyle: 'short',
    }).format(new Date(value));
  }
}
 