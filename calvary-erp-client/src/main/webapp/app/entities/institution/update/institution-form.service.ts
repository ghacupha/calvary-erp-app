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

import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IInstitution, NewInstitution } from '../institution.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInstitution for edit and NewInstitutionFormGroupInput for create.
 */
type InstitutionFormGroupInput = IInstitution | PartialWithRequiredKeyOf<NewInstitution>;

type InstitutionFormDefaults = Pick<NewInstitution, 'id'>;

type InstitutionFormGroupContent = {
  id: FormControl<IInstitution['id'] | NewInstitution['id']>;
  name: FormControl<IInstitution['name']>;
};

export type InstitutionFormGroup = FormGroup<InstitutionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InstitutionFormService {
  createInstitutionFormGroup(institution: InstitutionFormGroupInput = { id: null }): InstitutionFormGroup {
    const institutionRawValue = {
      ...this.getFormDefaults(),
      ...institution,
    };
    return new FormGroup<InstitutionFormGroupContent>({
      id: new FormControl(
        { value: institutionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(institutionRawValue.name, {
        validators: [Validators.required],
      }),
    });
  }

  getInstitution(form: InstitutionFormGroup): IInstitution | NewInstitution {
    return form.getRawValue() as IInstitution | NewInstitution;
  }

  resetForm(form: InstitutionFormGroup, institution: InstitutionFormGroupInput): void {
    const institutionRawValue = { ...this.getFormDefaults(), ...institution };
    form.reset(
      {
        ...institutionRawValue,
        id: { value: institutionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): InstitutionFormDefaults {
    return {
      id: null,
    };
  }
}
