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

import { Component, Input, OnInit, TemplateRef, inject, OnChanges } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IInstitution } from 'app/entities/institution/institution.model';
import { InstitutionService } from 'app/entities/institution/service/institution.service';
import { ApplicationUserService } from '../service/application-user.service';
import { IApplicationUser } from '../application-user.model';
import { ApplicationUserFormGroup, ApplicationUserFormService } from './application-user-form.service';

@Component({
  standalone: true,
  selector: 'jhi-application-user-update',
  templateUrl: './application-user-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ApplicationUserUpdateComponent implements OnInit, OnChanges {
  @Input() customActivatedTemplate?: TemplateRef<any> | null = null;
  @Input() customInstitutionTemplate?: TemplateRef<any> | null = null;
  @Input() selectedInstitution!: IInstitution;

  isSaving = false;
  applicationUser: IApplicationUser | null = null;

  usersSharedCollection: IUser[] = [];
  institutionsSharedCollection: IInstitution[] = [];

  protected applicationUserService = inject(ApplicationUserService);
  protected applicationUserFormService = inject(ApplicationUserFormService);
  protected userService = inject(UserService);
  protected institutionService = inject(InstitutionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ApplicationUserFormGroup = this.applicationUserFormService.createApplicationUserFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareInstitution = (o1: IInstitution | null, o2: IInstitution | null): boolean => this.institutionService.compareInstitution(o1, o2);

  ngOnChanges(): void {
    this.editForm.patchValue({ institution: this.selectedInstitution });
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ applicationUser }) => {
      this.applicationUser = applicationUser;
      if (applicationUser) {
        this.updateForm(applicationUser);
      }
      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const applicationUser = this.applicationUserFormService.getApplicationUser(this.editForm);
    if (applicationUser.id !== null) {
      this.subscribeToSaveResponse(this.applicationUserService.update(applicationUser));
    } else {
      this.subscribeToSaveResponse(this.applicationUserService.create(applicationUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApplicationUser>>): void {
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

  protected updateForm(applicationUser: IApplicationUser): void {
    this.applicationUser = applicationUser;
    this.applicationUserFormService.resetForm(this.editForm, applicationUser);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      applicationUser.systemUser,
    );
    this.institutionsSharedCollection = this.institutionService.addInstitutionToCollectionIfMissing<IInstitution>(
      this.institutionsSharedCollection,
      applicationUser.institution,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.applicationUser?.systemUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.institutionService
      .query()
      .pipe(map((res: HttpResponse<IInstitution[]>) => res.body ?? []))
      .pipe(
        map((institutions: IInstitution[]) =>
          this.institutionService.addInstitutionToCollectionIfMissing<IInstitution>(institutions, this.applicationUser?.institution),
        ),
      )
      .subscribe((institutions: IInstitution[]) => (this.institutionsSharedCollection = institutions));
  }
}
