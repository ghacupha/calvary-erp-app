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

import { Component, OnInit } from '@angular/core';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ApplicationUserUpdateComponent } from 'app/entities/application-user/update/application-user-update.component';
import { SlideToggleComponent } from 'app/extensions/shared/slide-toggle.component';
import { M21InstitutionFormControlComponent } from '../../../form-components/institution/m21-institution-form-control.component';
import { IInstitution } from '../../institution/institution.model';

@Component({
  standalone: true,
  selector: 'jhi-erp-application-user-update',
  templateUrl: './application-user-update.component.html',
  imports: [
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    ApplicationUserUpdateComponent,
    SlideToggleComponent,
    M21InstitutionFormControlComponent,
    // HasAnyAuthorityDirective,
  ],
})
export class ERPApplicationUserUpdateComponent extends ApplicationUserUpdateComponent implements OnInit {
  selectedInstitution!: IInstitution;

  updateInstitution(value: IInstitution): void {
    this.selectedInstitution = value;
  }
}
