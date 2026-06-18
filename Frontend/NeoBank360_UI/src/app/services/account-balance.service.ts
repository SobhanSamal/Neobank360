import { Injectable } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { AccountData } from './user-api.service';
 
@Injectable({
  providedIn: 'root'
})
export class AccountBalanceService {
  /**
   * Emits updated account data whenever balance changes after transaction
   * Components subscribing to this will receive real-time balance updates
   */
  balanceUpdated = new EventEmitter<AccountData>();
 
  /**
   * Emits when a new account is created
   * Components can listen to reload their account list
   */
  accountCreated = new EventEmitter<AccountData>();
 
  /**
   * Emit when transaction completes (deposit/withdraw)
   * Pass the updated account with new balance
   */
  notifyBalanceChange(account: AccountData): void {
    this.balanceUpdated.emit(account);
  }
 
  /**
   * Emit when a new account is created
   * Pass the newly created account
   */
  notifyAccountCreated(account: AccountData): void {
    this.accountCreated.emit(account);
  }
}
 