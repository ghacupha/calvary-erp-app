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

import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IAuthority } from '../authority.model';
import { AuthorityService } from '../service/authority.service';
import { AuthorityFormGroup, AuthorityFormService } from './authority-form.service';

@Component({
  standalone: true,
  selector: 'jhi-authority-update',
  templateUrl: './authority-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AuthorityUpdateComponent implements OnInit {
  isSaving = false;
  authority: IAuthority | null = null;

  protected authorityService = inject(AuthorityService);
  protected authorityFormService = inject(AuthorityFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AuthorityFormGroup = this.authorityFormService.createAuthorityFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ authority }) => {
      this.authority = authority;
      if (authority) {
        this.updateForm(authority);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const authority = this.authorityFormService.getAuthority(this.editForm);
    this.subscribeToSaveResponse(this.authorityService.create(authority));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAuthority>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(authority: IAuthority): void {
    this.authority = authority;
    this.authorityFormService.resetForm(this.editForm, authority);
  }
}
