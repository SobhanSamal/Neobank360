import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AdminDashboardService, SystemHealthData } 
from '../../services/admin-dashboard.service';

@Component({
  selector: 'app-system-health',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './system-health.component.html',
  styleUrls: ['./system-health.component.css'], 
})
export class SystemHealthComponent implements OnInit {

  loading = true;
  error = '';
  health: SystemHealthData | null = null; // ✅ FIX TYPE

  constructor(private readonly adminService: AdminDashboardService) {}

  ngOnInit(): void {
    this.loadHealth();

    // ✅ Auto refresh every 5 sec
    setInterval(() => this.loadHealth(), 5000);
  }

  loadHealth(): void {
    this.loading = true;
    this.error = '';

    this.adminService.getSystemHealth().subscribe({
      next: (res) => {
        console.log('System Health:', res); // ✅ debug
        this.health = res;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Unable to load system health.';
        this.loading = false;
      },
    });
  }

  formatUptime(seconds: number): string {

    if (!seconds) return '0s';

    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;

    return `${h}h ${m}m ${s}s`;
  }
}