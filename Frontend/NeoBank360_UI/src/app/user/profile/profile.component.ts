import { CommonModule } from '@angular/common';

import { Component, OnInit } from '@angular/core';

import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';


 

import { NavbarComponent } from '../../navbar/navbar.component';

import { AuthService } from '../../services/auth.service';

import { UserApiService } from '../../services/user-api.service';


 

type ExtraProfileFields = {

  gender: string;

  dob: string;

  mobile: string;

  alternateMobile: string;

  addressLine1: string;

  state: string;

  district: string;

  pincode: string;

};


 

function noSameDigits(control: AbstractControl) {

  const value = control.value;

  return value && /^(\d)\1+$/.test(value) ? { sameDigits: true } : null;

}


 

@Component({

  selector: 'app-profile',

  standalone: true,

  imports: [CommonModule, ReactiveFormsModule],

  templateUrl: './profile.component.html',

  styleUrls: ['./profile.component.css'],

})

export class ProfileComponent implements OnInit {

  loading = false;

  saving = false;

  success = '';

  error = '';


 

  readonly genderOptions = ['Male', 'Female', 'Other'];


 

  readonly locationOptions: Record<string, string[]> = {

    Odisha: ['Angul', 'Balasore', 'Cuttack', 'Khordha', 'Puri', 'Sambalpur'],

    Telangana: ['Hyderabad', 'Karimnagar', 'Warangal'],

    Karnataka: ['Bengaluru Urban', 'Mysuru', 'Mangaluru'],

    Maharashtra: ['Mumbai City', 'Pune', 'Nagpur'],

  };


 

  readonly form;


 

  private readonly storageKeyPrefix = 'nb360_profile_extra_';


 

  constructor(

    private readonly fb: FormBuilder,

    private readonly userApi: UserApiService,

    private readonly authService: AuthService

  ) {

    this.form = this.fb.nonNullable.group({

      fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],

      email: [{ value: '', disabled: true }],

      gender: ['', Validators.required],

      dob: ['', Validators.required],

      mobile: [

        '',

        [

          Validators.required,

          Validators.pattern(/^[6-9]\d{9}$/),

          Validators.minLength(10),

          Validators.maxLength(10),

          noSameDigits,

        ],

      ],

      alternateMobile: [

        '',

        [Validators.pattern(/^[6-9]\d{9}$/), Validators.minLength(10), Validators.maxLength(10)],

      ],

      addressLine1: ['', [Validators.required, Validators.minLength(5)]],

      state: ['', Validators.required],

      district: ['', Validators.required],

      pincode: ['', [Validators.required, Validators.pattern(/^[1-9][0-9]{5}$/)]],

      role: [{ value: '', disabled: true }],

      active: [{ value: '', disabled: true }],

    });

  }


 

  ngOnInit(): void {

    const sessionEmail = this.authService.getUserEmail();

    if (sessionEmail) {

      this.form.controls.email.setValue(sessionEmail);

    }


 

    this.loadExtraFields();

    this.loadProfile();

  }


 

  get states(): string[] {

    return Object.keys(this.locationOptions);

  }


 

  get districts(): string[] {

    const state = this.form.controls.state.value;

    return state ? this.locationOptions[state] || [] : [];

  }


 

  loadProfile(): void {

    this.loading = true;

    this.error = '';

    this.success = '';


 

    this.userApi.getProfile().subscribe({

      next: (profile) => {

        this.loading = false;

        this.form.patchValue({

          fullName: profile.fullName || '',

          email: profile.email || this.authService.getUserEmail() || '',

          role: profile.role || '',


        });

      },

      error: () => {

        this.loading = false;

        this.error = 'Failed to load profile';

      },

    });

  }


 

  save(): void {

    if (this.form.invalid) {

      this.form.markAllAsTouched();

      return;

    }


 

    this.saving = true;

    this.success = '';

    this.error = '';


 

    this.saveExtraFields();


 

    this.userApi.updateProfile({

      fullName: this.form.controls.fullName.value.trim(),

    }).subscribe({

      next: (profile) => {

        this.saving = false;

        this.success = 'Profile updated successfully';

        this.form.patchValue({

          fullName: profile.fullName || '',

          email: profile.email || this.authService.getUserEmail() || '',

          role: profile.role || '',


        });

      },

      error: () => {

        this.saving = false;

        this.error = 'Profile update failed';

      },

    });

  }


 

  hasError(controlName: string): boolean {

    const control = this.form.get(controlName);

    return !!control && control.invalid && (control.dirty || control.touched);

  }


 

  onMobileInput(event: Event): void {

    this.setDigitValue(event, 'mobile', 10);

  }


 

  onAlternateMobileInput(event: Event): void {

    this.setDigitValue(event, 'alternateMobile', 10);

  }


 

  onPincodeInput(event: Event): void {

    this.setDigitValue(event, 'pincode', 6);

  }


 

  private setDigitValue(event: Event, controlName: keyof ExtraProfileFields, maxLength: number): void {

    const input = event.target as HTMLInputElement;

    const value = input.value.replace(/\D/g, '').slice(0, maxLength);

    input.value = value;

    this.form.get(controlName)?.setValue(value, { emitEvent: false });

  }


 

  private loadExtraFields(): void {

    if (typeof window === 'undefined') {

      return;

    }


 

    try {

      const raw = sessionStorage.getItem(this.storageKey);

      if (raw) {

        this.form.patchValue(JSON.parse(raw) as ExtraProfileFields);

      }

    } catch {

      sessionStorage.removeItem(this.storageKey);

    }

  }


 

  private saveExtraFields(): void {

    if (typeof window === 'undefined') {

      return;

    }


 

    const raw = this.form.getRawValue();

    const extra: ExtraProfileFields = {

      gender: raw.gender,

      dob: raw.dob,

      mobile: raw.mobile,

      alternateMobile: raw.alternateMobile,

      addressLine1: raw.addressLine1,

      state: raw.state,

      district: raw.district,

      pincode: raw.pincode,

    };


 

    sessionStorage.setItem(this.storageKey, JSON.stringify(extra));

  }


 

  private get storageKey(): string {

    return `${this.storageKeyPrefix}${this.authService.getUserEmail() || 'guest'}`;

  }

}


 