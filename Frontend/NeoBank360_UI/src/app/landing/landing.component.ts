import { Component } from '@angular/core';

import { CommonModule } from '@angular/common';

import { RouterLink } from '@angular/router';

import { NavbarComponent } from '../navbar/navbar.component';


 

@Component({

  selector: 'app-landing',

  standalone: true,

  imports: [CommonModule, RouterLink, NavbarComponent],

  template: `

    <app-navbar></app-navbar>


 

    <div class="landing-container">


 

      <!-- HERO SECTION -->

      <section class="hero-section">

        <div class="hero-left">

          <p class="tagline">SMART DIGITAL BANKING</p>


 

          <h1>

            Manage Your Money <br>

            Smarter with NeoBank360

          </h1>


 

          <p class="hero-text">

            Open accounts, transfer funds, track budgets,

            pay bills, and earn rewards — all in one

            secure digital banking platform.

          </p>


 

          <div class="hero-buttons">

            <a routerLink="/register" class="primary-btn">

              Open Account

            </a>


 

            <a routerLink="/login" class="secondary-btn">

              Login

            </a>

          </div>


 

          <div class="stats-row">

            <div class="stat-box">

              <h2>10K+</h2>

              <p>Users</p>

            </div>


 

            <div class="stat-box">

              <h2>₹50Cr+</h2>

              <p>Transactions</p>

            </div>


 

            <div class="stat-box">

              <h2>99.9%</h2>

              <p>Security</p>

            </div>

          </div>

        </div>


 

        <div class="hero-right">

          <div class="dashboard-card">

            <div class="balance-box">

              <p>Total Balance</p>

              <h2>₹84,250</h2>

            </div>


 

            <div class="transaction-box">

              <div>

                <span>Salary Credit</span>

                <strong class="green">+₹42,000</strong>

              </div>


 

              <div>

                <span>Electric Bill</span>

                <strong class="red">-₹2,150</strong>

              </div>


 

              <div>

                <span>Rewards Earned</span>

                <strong class="blue">+320 pts</strong>

              </div>

            </div>

          </div>

        </div>

      </section>


 

      <!-- FEATURES -->

      <section class="features-section">

        <h2>Everything You Need</h2>


 

        <div class="feature-grid">

          <div class="feature-card">

            <h3>Accounts</h3>

            <p>Create savings/current accounts instantly.</p>

          </div>


 

          <div class="feature-card">

            <h3>Transactions</h3>

            <p>Fast and secure money transfer.</p>

          </div>


 

          <div class="feature-card">

            <h3>Budget</h3>

            <p>Track monthly spending easily.</p>

          </div>


 

          <div class="feature-card">

            <h3>Bills</h3>

            <p>Pay utility bills on time.</p>

          </div>


 

          <div class="feature-card">

            <h3>Rewards</h3>

            <p>Earn cashback points.</p>

          </div>


 

          <div class="feature-card">

            <h3>Security</h3>

            <p>JWT secured banking platform.</p>

          </div>

        </div>

      </section>


 

      <!-- CTA -->

      <section class="cta-section">

        <h2>Start Your Banking Journey Today</h2>

        <a routerLink="/register" class="cta-btn">Get Started</a>

      </section>


 

    </div>

  `,

styles: [`
  * {
    box-sizing: border-box;
  }

  .landing-container {
    background: #f8fafc;
    min-height: 100vh;
    font-family: Arial, sans-serif;
    overflow-x: hidden;
  }

  /* =========================
     HERO SECTION
  ========================== */
  .hero-section {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    padding: clamp(30px, 6vw, 70px);
    align-items: center;
    gap: 40px;
    background: linear-gradient(135deg, #eff6ff, #dbeafe);
  }

  .tagline {
    color: #2563eb;
    font-weight: bold;
    letter-spacing: 2px;
    font-size: 14px;
  }

  .hero-left h1 {
    font-size: clamp(2rem, 4vw, 3.5rem);
    color: #0f172a;
    margin: 20px 0;
    line-height: 1.2;
  }

  .hero-text {
    color: #475569;
    font-size: 18px;
    line-height: 1.7;
    max-width: 520px;
  }

  .hero-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
    margin-top: 25px;
  }

  .primary-btn,
  .secondary-btn,
  .cta-btn {
    padding: 14px 28px;
    border-radius: 10px;
    text-decoration: none;
    font-weight: 600;
    white-space: nowrap;
  }

  .primary-btn {
    background: #2563eb;
    color: #fff;
  }

  .secondary-btn {
    background: #fff;
    color: #2563eb;
    border: 1px solid #2563eb;
  }

  /* =========================
     STATS
  ========================== */
  .stats-row {
    display: flex;
    gap: 20px;
    margin-top: 40px;
    flex-wrap: wrap;
  }

  .stat-box {
    background: white;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.08);
    text-align: center;
    min-width: 120px;
    flex: 1;
  }

  /* =========================
     DASHBOARD CARD
  ========================== */
  .dashboard-card {
    background: #0f172a;
    color: white;
    padding: 30px;
    border-radius: 20px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.2);
    max-width: 420px;
    margin: auto;
  }

  .balance-box {
    margin-bottom: 25px;
    text-align: center;
  }

  .transaction-box div {
    background: rgba(255,255,255,0.1);
    padding: 14px;
    margin-bottom: 14px;
    border-radius: 10px;
    display: flex;
    justify-content: space-between;
    font-size: 15px;
  }

  .green { color: #22c55e; }
  .red { color: #ef4444; }
  .blue { color: #38bdf8; }

  /* =========================
     FEATURES
  ========================== */
  .features-section {
    padding: clamp(40px, 6vw, 70px);
    text-align: center;
  }

  .feature-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 20px;
    margin-top: 30px;
  }

  .feature-card {
    background: white;
    padding: 25px;
    border-radius: 15px;
    box-shadow: 0 5px 20px rgba(0,0,0,0.08);
  }

  /* =========================
     CTA
  ========================== */
  .cta-section {
    background: #0f172a;
    color: white;
    text-align: center;
    padding: clamp(40px, 6vw, 60px);
  }

  .cta-btn {
    display: inline-block;
    margin-top: 20px;
    background: white;
    color: #0f172a;
  }

  /* =========================
     MOBILE BREAKPOINTS
  ========================== */
  @media (max-width: 768px) {
    .hero-section {
      grid-template-columns: 1fr;
      text-align: center;
    }

    .hero-text {
      margin: auto;
    }

    .hero-buttons {
      justify-content: center;
    }

    .stats-row {
      justify-content: center;
    }

    .dashboard-card {
      width: 100%;
    }
  }

  @media (max-width: 480px) {
    .hero-left h1 {
      font-size: 2rem;
    }

    .primary-btn,
    .secondary-btn {
      width: 100%;
      text-align: center;
    }
  }
`]

})

export class LandingComponent {}


 