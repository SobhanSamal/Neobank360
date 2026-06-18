import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <section style="min-height:calc(100vh - 52px);display:grid;place-items:center;background:#f8fafc;padding:16px;">
      <div style="max-width:640px;background:#fff;border-radius:12px;padding:24px;box-shadow:0 10px 24px rgba(15,23,42,.1);text-align:center;">
        <h2 style="margin:0 0 10px;color:#0f172a;">NeoBank360 Dashboard</h2>
        <p style="margin:0 0 16px;color:#334155;">Day 3 secured screen. You are logged in.</p>
        <p style="margin:0 0 18px;color:#0f172a;"><strong>Email:</strong> {{ authService.getUserEmail() || 'N/A' }}</p>
      </div>
    </section>
  `,
})
export class DashboardComponent implements OnInit {
  constructor(public readonly authService: AuthService, private readonly router: Router) {}

  ngOnInit(): void {
    this.router.navigate([this.authService.getDashboardRoute()]);
  }
}
