import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login-placeholder',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section style="min-height:100vh;display:grid;place-items:center;background:#f8fafc;padding:16px;">
      <div style="max-width:540px;background:#fff;border-radius:12px;padding:24px;box-shadow:0 10px 24px rgba(15,23,42,.1);text-align:center;">
        <h2 style="margin:0 0 12px;color:#0f172a;">Login screen will be implemented on Day 3</h2>
        <p style="margin:0 0 16px;color:#334155;">Day 2 registration is complete and wired to backend.</p>
        <a routerLink="/register" style="color:#4f46e5;text-decoration:underline;">Back to registration</a>
      </div>
    </section>
  `,
})
export class LoginPlaceholderComponent {}
