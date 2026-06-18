import { Routes } from '@angular/router';
 
/* Layout */
import { LayoutComponent } from './layout/layout.component';
 
/* Auth */
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';
import { authGuard } from './auth/auth.guard';
 
/* Landing */
import { LandingComponent } from './landing/landing.component';
 
/* Dashboard */
import { DashboardComponent } from './dashboard/dashboard.component';
import { AdminDashboardComponent } from './dashboard/admin/admin-dashboard.component';
import { CustomerDashboardComponent } from './dashboard/customer/customer-dashboard.component';
 
/* Guards */
import { roleGuard } from './guards/role.guard';
 
/* User */
import { ProfileComponent } from './user/profile/profile.component';
 
/* Core Features */
import { AccountComponent } from './account/account.component';
import { TransactionComponent } from './transaction/transaction.component';
 
/* Budget */
import { BudgetCreateComponent } from './budget/create/budget-create.component';
import { BudgetDashboardComponent } from './budget/dashboard/budget-dashboard.component';
 
/* Bills */
import { BillCreateComponent } from './bill/create/bill-create.component';
import { BillListComponent } from './bill/list/bill-list.component';
 
/* Rewards */
import { RewardDashboardComponent } from './reward/dashboard/reward-dashboard.component';
import { RewardHistoryComponent } from './reward/history/reward-history.component';
 
/* Loan */
import { LoanProductComponent } from './loan/loan-product/loan-product.component';
import { ApplyLoanComponent } from './loan/apply-loan/apply-loan.component';
import { LoanApprovalComponent } from './loan/loan-approval/loan-approval.component';
import { MyLoansComponent } from './loan/my-loans/my-loans.component';
import { RepaymentScheduleComponent } from './loan/repayment-schedule/repayment-schedule.component';
import { InsightsDashboardComponent } from './insights/insights-dashboard.component';
 
 
import { PendingApprovalsComponent }
from './dashboard/pending-approvals/pending-approvals.component';
import { UserManagementComponent } from './dashboard/user-management/user-management.component';
import { SystemHealthComponent } from './dashboard/system-health/system-health.component';
import { AdminAnalyticsComponent } from './analytics/admin/admin-analytics.component';
import { CustomerAnalyticsComponent } from './analytics/customer/customer-analytics.component';
import { SystemLogsComponent } from './analytics/system-logs/system-logs.component';
 
 
export const routes: Routes = [
 
  /* ✅ Default */
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'landing'
  },
 
  /* ✅ Public */
  {
    path: 'landing',
    component: LandingComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
 
  /* ✅ Protected Layout */
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
 
    children: [
 
      /* ✅ Dashboard */
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'dashboard/admin',
        component: AdminDashboardComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
 
      {
  path: 'admin/pending-approvals',
  component: PendingApprovalsComponent,
  canActivate: [roleGuard],
  data: { roles: ['ADMIN'] }
},
      {
        path: 'admin/system-health',
        component: SystemHealthComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      // {
      //   path: 'admin/system-logs',
      //   component: AdminSystemLogsComponent,
      //   canActivate: [roleGuard],
      //   data: { roles: ['ADMIN'] }
      // },
      {
        path: 'dashboard/customer',
        component: CustomerDashboardComponent,
        canActivate: [roleGuard],
        data: { roles: ['CUSTOMER', 'ADMIN'] }
      },
 
      /* ✅ DAY‑23: Apply Loan */
      {
        path: 'dashboard/customer/apply-loan',
        component: ApplyLoanComponent,
        canActivate: [roleGuard],
        data: { roles: ['CUSTOMER'] }
      },
      {
        path: 'dashboard/customer/my-loans',
        component: MyLoansComponent,
        canActivate: [roleGuard],
        data: { roles: ['CUSTOMER', 'ADMIN'] }
      },
      {
        path: 'dashboard/customer/my-loans/:accountId/repayments',
        component: RepaymentScheduleComponent,
        canActivate: [roleGuard],
        data: { roles: ['CUSTOMER', 'ADMIN'] }
      },
 
      /* ✅ Accounts */
      {
        path: 'accounts',
        component: AccountComponent
      },
 
      /* ✅ Transactions */
      {
        path: 'transactions',
        component: TransactionComponent
      },
 
      /* ✅ Budget */
      {
        path: 'budget',
        component: BudgetDashboardComponent
      },
      {
        path: 'budget/create',
        component: BudgetCreateComponent
      },
 
      /* ✅ Bills */
      {
        path: 'bills',
        component: BillListComponent
      },
      {
        path: 'bills/create',
        component: BillCreateComponent
      },
 
      /* ✅ Rewards */
      {
        path: 'rewards',
        component: RewardDashboardComponent
      },
      {
        path: 'rewards/history',
        component: RewardHistoryComponent
      },
      {
        path: 'insights',
        component: InsightsDashboardComponent,
        canActivate: [roleGuard],
        data: { roles: ['CUSTOMER'] }
      },
      {
        path: 'analytics',
        component: AdminAnalyticsComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'admin/system-logs',
        component: SystemLogsComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'customer-analytics',
        component: CustomerAnalyticsComponent,
        canActivate: [roleGuard],
        data: { roles: ['CUSTOMER'] }
      },
 
      /* ✅ Loan Products (ADMIN) */
      {
        path: 'loan-products',
        component: LoanProductComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
 
      /* ✅ ✅ ✅ DAY‑24: Loan Approval (ADMIN) */
      {
        path: 'loan-approval',
        component: LoanApprovalComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
 
      {
        path: 'admin/user-management',
        component: UserManagementComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
 
 
      /* ✅ Profile */
      {
        path: 'profile',
        component: ProfileComponent
      }
 
    ]
  },
 
  /* ✅ Fallback */
  {
    path: '**',
    redirectTo: 'landing'
  }
 
];
 