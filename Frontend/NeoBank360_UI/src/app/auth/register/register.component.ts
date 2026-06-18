import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  ApiError,
  AuthService,
  RegisterRequest,
  UserResponse,
} from '../../services/auth.service';
import { NavbarComponent } from '../../navbar/navbar.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NavbarComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  protected loading = false;
  protected submitted = false;
  protected successMessage = '';
  protected errorMessage = '';

  protected readonly registerForm: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.registerForm = this.fb.group(
      {
        fullName: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(8),
            Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).+$/),
          ],
        ],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  protected onSubmit(): void {
    this.submitted = true;
    this.successMessage = '';
    this.errorMessage = '';

    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;

    const payload = this.registerForm.value as RegisterRequest;
    this.authService.register(payload).subscribe({
      next: (response: UserResponse) => {
        this.loading = false;
        this.successMessage = `Registration successful. Welcome ${response.fullName}.`;
        this.registerForm.reset();
        this.submitted = false;
        this.cdr.detectChanges(); // Render success message immediately

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1200);
      },
      error: (error: ApiError) => {
        this.loading = false;
        if (error.status === 409) {
          this.errorMessage = 'Email is already registered.';
          this.cdr.detectChanges(); // Render error immediately
          return;
        }
        this.errorMessage = error.message;
        this.cdr.detectChanges(); // Render error immediately
      },
    });
  }

  protected hasError(controlName: string, errorName: string): boolean {
    const control = this.registerForm.get(controlName);
    return !!control && control.hasError(errorName) && (control.touched || this.submitted);
  }

  protected passwordsDoNotMatch(): boolean {
    return !!this.registerForm.hasError('passwordMismatch') &&
      (this.registerForm.get('confirmPassword')?.touched || this.submitted);
  }

  private passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;

    if (!password || !confirmPassword) {
      return null;
    }

    return password === confirmPassword ? null : { passwordMismatch: true };
  }
}
