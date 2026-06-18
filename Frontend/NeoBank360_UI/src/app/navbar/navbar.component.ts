import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Subject, interval, takeUntil } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],

  template: `
    <nav class="navbar">

      <!-- Left -->
      <h2 class="logo" routerLink="/landing">
        NeoBank360
      </h2>

      <!-- Right -->
      <div class="nav-right">

      <ng-container *ngIf="!isLoggedIn">

          <a routerLink="/login" class="login-btn">
            Login
          </a>

          <a routerLink="/register" class="register-btn">
            Register
          </a>

        </ng-container>

        <!-- Logged-in info -->
        <span *ngIf="isLoggedIn" class="email">
          {{ userEmail }}
        </span>

        <!-- Logout -->
        <button
          *ngIf="isLoggedIn"
          (click)="onLogout()"
          class="logout-btn"
        >
          Logout
        </button>

      </div>

    </nav>
  `,

  styles: [`
    /* ✅ FIXED NAVBAR */
    .navbar {
      position: sticky;   /* ✅ FIX */
      top: 0;
      left: 0;
      right: 0;
      height: 60px;

      display: flex;
      justify-content: space-between;
      align-items: center;

      padding: 0 20px;

      background: linear-gradient(to right, #0f172a, #1e293b);
      color: #ffffff;

      z-index: 9999;
      box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    }

    
/* ✅ LOGIN */
    .login-btn {
      text-decoration: none;
      background: #2563eb;
      color: white;
      padding: 8px 14px;
      border-radius: 8px;
      font-weight: 600;
    }

    /* ✅ REGISTER */
    .register-btn {
      text-decoration: none;
      background: white;
      color: #0f172a;
      padding: 8px 14px;
      border-radius: 8px;
      font-weight: 600;
    }


    .logo {
      margin: 0;
      font-size: 18px;
      cursor: pointer;
      color: #ffffff;
    }

    .nav-right {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .email {
      color: #cbd5e1;
      font-size: 14px;
    }

    .logout-btn {
      background: #ef4444;
      color: white;
      border: none;
      padding: 8px 14px;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: 0.2s;
    }

    .logout-btn:hover {
      background: #dc2626;
    }
  `]
})
export class NavbarComponent implements OnInit, OnDestroy {

  isLoggedIn = false;
  userEmail: string | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.updateLoginStatus();

    interval(500)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.updateLoginStatus();
        this.cdr.detectChanges();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private updateLoginStatus(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.userEmail = this.authService.getUserEmail();
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
