import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { App } from './app';
import { AppRoutingModule } from './app-routing.module';

/* ✅ Angular Material */
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatStepperModule } from '@angular/material/stepper';
import { MatCardModule } from '@angular/material/card';   // ✅ ADD (for KPI cards)


@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    App,

    /* ✅ Material */
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatStepperModule,
    MatCardModule,          // ✅ KPI cards

  ],
  bootstrap: [App],
})
export class AppModule {}