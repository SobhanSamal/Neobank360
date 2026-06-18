import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
 
import { NavbarComponent } from '../../navbar/navbar.component';
import {
  BudgetApiService,
  BudgetCategory,
  BudgetRequestData
} from '../../services/budget-api.service';
 
@Component({
  selector: 'app-budget-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './budget-create.component.html',
  styleUrl: './budget-create.component.css'
})
export class BudgetCreateComponent {
 
  creating = false;
  successMessage = '';
  errorMessage = '';
 
  readonly categories: BudgetCategory[] = [
    'GROCERIES',
    'UTILITIES',
    'RENT',
    'ENTERTAINMENT',
    'TRANSFER',
    'OTHER'
  ];
 
  readonly form;
 
  constructor(
    private readonly fb: FormBuilder,
    private readonly budgetApi: BudgetApiService,
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {
 
    const month = new Date().toISOString().slice(0, 7);
 
    this.form = this.fb.nonNullable.group({
      category: ['GROCERIES' as BudgetCategory, Validators.required],
      budgetMonth: [month, Validators.required],
      limitAmount: [1000, [Validators.required, Validators.min(0.01)]]
    });
  }
 
  createBudget(): void {

   this.errorMessage = '';  this.successMessage = '';

  if (this.form.invalid) {
    return;
  }

  this.creating = true;
  const payload: BudgetRequestData = this.form.getRawValue();

  this.budgetApi.create(payload).subscribe({

    next: () => {
      this.creating = false;

      this.successMessage =
        'Budget created successfully. Redirecting to dashboard...';

      setTimeout(() => {
        this.router.navigate(['/budget']);
      }, 800);

      this.cdr.detectChanges();
    },

    error: (err) => {

      this.creating = false;

      // ✅ Handle 401 (stay same page)
      if (err.status === 401) {
        this.errorMessage = 'Unauthorized. Please try again.';
        this.cdr.detectChanges();
        return;
      }

      // ✅ Handle 400
      if (err.status === 400) {
        this.errorMessage =
          err.message || 'Invalid request.';
        this.cdr.detectChanges();
        return;
      }

      // ✅ All other errors
      this.errorMessage =
        err.message || 'Failed to create budget.';
      this.cdr.detectChanges();
    }

  });  // ✅ ✅ ✅ THIS WAS MISSING
}

/* ✅ THIS MUST BE OUTSIDE subscribe */

goToDashboard(): void {
  this.router.navigate(['/budget']);
}
}