import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
selector: 'app-layout',
standalone: true,
imports: [RouterOutlet, NavbarComponent, SidebarComponent],

template: `
<app-navbar></app-navbar>

<div style="display: flex;">
  <app-sidebar></app-sidebar>

  <div
    style="
      flex: 1;
      padding: 20px;
      background: #f1f5f9;
      min-height: calc(100vh - 60px);
    "
  >
    <router-outlet></router-outlet>
  </div>
</div>
`
})
export class LayoutComponent {}