import {
  AfterViewInit,
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { LoanService, RepaymentSchedule } from '../../services/loan.service';

@Component({
  selector: 'app-repayment-schedule',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatPaginatorModule,
    MatSortModule,
    MatTableModule
  ],
  templateUrl: './repayment-schedule.component.html',
  styleUrls: ['./repayment-schedule.component.css']
})
export class RepaymentScheduleComponent
  implements OnInit, AfterViewInit, OnDestroy {

  accountId = 0;
  loading = false;
  error = '';
  statusFilter = '';

  showToast = false;   // ✅ Toast

  displayedColumns = [
    'instalmentNumber',
    'dueDate',
    'emiAmount',
    'principalComponent',
    'interestComponent',
    'paymentStatus',
    'action'
  ];

  dataSource = new MatTableDataSource<RepaymentSchedule>([]);

  refreshInterval: any;  // ✅ Auto refresh

  @ViewChild(MatPaginator) paginator?: MatPaginator;
  @ViewChild(MatSort) sort?: MatSort;

  constructor(
    private route: ActivatedRoute,
    private loanService: LoanService,
    private cdr: ChangeDetectorRef   // ✅ FIX
  ) {}

  ngOnInit(): void {
    this.accountId = Number(this.route.snapshot.paramMap.get('accountId'));

    this.loadSchedule();

    // ✅ Auto refresh every 10 sec
    this.refreshInterval = setInterval(() => {
      this.loadSchedule();
    }, 10000);
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.dataSource.paginator = this.paginator ?? null;
      this.dataSource.sort = this.sort ?? null;
    });
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  /* ✅ Load Schedule */
  loadSchedule(showToast = false): void {
    this.loading = true;
    this.error = '';

    this.loanService.getRepaymentSchedule(
      this.accountId,
      {
        status: this.statusFilter,
        page: 0,
        size: 1000
      }
    ).subscribe({
      next: (schedule) => {
        this.dataSource.data = schedule;
        this.loading = false;

        this.cdr.detectChanges(); 
        if (showToast) {
          this.showToastMessage(); 
        }
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load repayment schedule.';
        this.loading = false;
      }
    });
  }

  /* ✅ Status Filter */
  onStatusChange(status: string): void {
    this.statusFilter = status;
    this.loadSchedule();
  }

  /* ✅ Pay EMI */
  pay(repayment: RepaymentSchedule): void {
    if (repayment.paymentStatus === 'PAID') {
      return;
    }

    this.loanService.payRepayment(this.accountId, repayment.id).subscribe({
      next: () => {
        this.loadSchedule(true);  // ✅ refresh + toast
      },
      error: () => {
        this.error = 'Failed to pay EMI.';
      }
    });
  }

  /* ✅ Toast */
  showToastMessage(): void {
    this.showToast = true;

    setTimeout(() => {
      this.showToast = false;
    }, 2000);
  }
}
