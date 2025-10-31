///
/// Erp System - Mark I No 2 (Beniah Series) Client 0.0.1-SNAPSHOT
/// Copyright © 2022 - 2025 Edwin Njeru (mailnjeru@gmail.com)
///
/// This program is free software: you can redistribute it and/or modify
/// it under the terms of the GNU General Public License as published by
/// the Free Software Foundation, either version 3 of the License, or
/// (at your option) any later version.
///
/// This program is distributed in the hope that it will be useful,
/// but WITHOUT ANY WARRANTY; without even the implied warranty of
/// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/// GNU General Public License for more details.
///
/// You should have received a copy of the GNU General Public License
/// along with this program. If not, see <http://www.gnu.org/licenses/>.
///

import { AfterViewInit, Component, ElementRef, inject, signal, viewChild } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import SharedModule from 'app/shared/shared.module';

import { PasswordResetInitService } from './password-reset-init.service';

@Component({
  standalone: true,
  selector: 'jhi-password-reset-init',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
  templateUrl: './password-reset-init.component.html',
})
export default class PasswordResetInitComponent implements AfterViewInit {
  email = viewChild.required<ElementRef>('email');

  success = signal(false);
  resetRequestForm;

  private readonly passwordResetInitService = inject(PasswordResetInitService);
  private readonly fb = inject(FormBuilder);

  constructor() {
    this.resetRequestForm = this.fb.group({
      email: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    });
  }

  ngAfterViewInit(): void {
    this.email().nativeElement.focus();
  }

  requestReset(): void {
    this.passwordResetInitService.save(this.resetRequestForm.get(['email'])!.value).subscribe(() => this.success.set(true));
  }
}
