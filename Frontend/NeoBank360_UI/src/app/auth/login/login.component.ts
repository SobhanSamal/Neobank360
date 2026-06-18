import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiError, AuthService, LoginRequest, LoginResponse } from '../../services/auth.service';
import { NavbarComponent } from '../../navbar/navbar.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NavbarComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  protected loading = false;
  protected submitted = false;
  protected errorMessage = '';
  protected showPassword = false;

  protected readonly loginForm: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  protected onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;

    const payload = this.loginForm.value as LoginRequest;
    this.authService.login(payload).subscribe({
      next: (response: LoginResponse) => {
        this.loading = false;
        this.cdr.detectChanges(); // Update UI immediately before navigation
        this.authService.saveSession(response);
        this.router.navigate([this.authService.getDashboardRoute()]);
      },
      error: (error: ApiError) => {
        this.loading = false;
        if (error.status === 401) {
          this.errorMessage = 'Invalid email or password.';
          this.cdr.detectChanges(); // Render error immediately
          return;
        }
        if (error.status === 403) {
          this.errorMessage = 'Your account is inactive. Contact support.';
          this.cdr.detectChanges(); // Render error immediately
          return;
        }
        this.errorMessage = error.message;
        this.cdr.detectChanges(); // Render error immediately
      },
    });
  }

  protected hasError(controlName: string, errorName: string): boolean {
    const control = this.loginForm.get(controlName);
    return !!control && control.hasError(errorName) && (control.touched || this.submitted);
  }

  protected togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
