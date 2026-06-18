import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { LoanService } from '../../services/loan.service';


 

@Component({

selector: 'app-loan-approval',

standalone: true,

imports: [CommonModule, FormsModule],

templateUrl: './loan-approval.component.html',

styleUrls: ['./loan-approval.component.css']

})

export class LoanApprovalComponent implements OnInit {


 

applications: any[] = [];

remarks: { [key: number]: string } = {};

statusFilter = 'PENDING';

page = 0;

pageSize = 5;


 

constructor(private service: LoanService) {}


 

ngOnInit(): void {

this.loadApplications();

}


 

// ✅ Load data

loadApplications() {

this.service.getAllApplications(this.statusFilter).subscribe({

next: (res) => {

console.log('Applications:', res);

this.applications = res;

},

error: (err) => {

console.error(err);

alert('❌ Failed to load applications');

}

});

}


 

get pagedApplications(): any[] {

const start = this.page * this.pageSize;

return this.applications.slice(start, start + this.pageSize);

}


 

get totalPages(): number {

return Math.max(Math.ceil(this.applications.length / this.pageSize), 1);

}


 

onFilterChange(): void {

this.page = 0;

this.loadApplications();

}


 

previousPage(): void {

this.page = Math.max(this.page - 1, 0);

}


 

nextPage(): void {

this.page = Math.min(this.page + 1, this.totalPages - 1);

}


 

// ✅ Approve

approve(app: any) {

this.makeDecision(app.id, 'APPROVED');

}


 

// ✅ Reject

reject(app: any) {

this.makeDecision(app.id, 'REJECTED');

}


 

// ✅ Common method

makeDecision(id: number, decision: string) {


 

const payload = {

decision: decision,

remarks: this.remarks[id] || ''

};


 

this.service.decideLoan(id, payload).subscribe({

next: () => {

alert(`✅ ${decision}`);

this.loadApplications();

},

error: () => {

alert('❌ Failed');

}

});

}

}


 