import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminDashboardService } from '../../services/admin-dashboard.service';

@Component({
  selector: 'app-pending-approvals',
  standalone: true,
  imports: [CommonModule, FormsModule], // ✅ added FormsModule
  templateUrl: './pending-approvals.component.html',
  styleUrls: ['./pending-approvals.component.css']
})
export class PendingApprovalsComponent implements OnInit {

  pendingList: any[] = [];
  loading = true;
  searchText = '';

  constructor(
    private service: AdminDashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();

    // ✅ Auto refresh every 15 sec
    setInterval(() => this.loadData(), 15000);
  }

  loadData() {
    this.service.getPendingApprovals().subscribe({
      next: (res: any[]) => {
        this.pendingList = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  // ✅ Filter logic
  get filteredList() {
    return this.pendingList.filter(item =>
      item.applicantName.toLowerCase().includes(this.searchText.toLowerCase()) ||
      item.productName.toLowerCase().includes(this.searchText.toLowerCase())
    );
  }

  // ✅ Sort by date
  sortByDate() {
    this.pendingList.sort((a, b) =>
      new Date(a.appliedAt).getTime() - new Date(b.appliedAt).getTime()
    );
  }

  review(item: any) {
    this.router.navigate(['/loan-approval'], {
      queryParams: { id: item.id }
    });
  }
}