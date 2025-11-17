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

import { IAuthority, NewAuthority } from '../authority.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { name: unknown }> = Partial<Omit<T, 'name'>> & { name: T['name'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAuthority for edit and NewAuthorityFormGroupInput for create.
 */
type AuthorityFormGroupInput = IAuthority | PartialWithRequiredKeyOf<NewAuthority>;

type AuthorityFormDefaults = Pick<NewAuthority, 'name'>;

type AuthorityFormGroupContent = {
  name: FormControl<IAuthority['name'] | NewAuthority['name']>;
};

export type AuthorityFormGroup = FormGroup<AuthorityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AuthorityFormService {
  createAuthorityFormGroup(authority: AuthorityFormGroupInput = { name: null }): AuthorityFormGroup {
    const authorityRawValue = {
      ...this.getFormDefaults(),
      ...authority,
    };
    return new FormGroup<AuthorityFormGroupContent>({
      name: new FormControl(
        { value: authorityRawValue.name, disabled: authorityRawValue.name !== null },
        {
          nonNullable: true,
          validators: [Validators.required, Validators.maxLength(50)],
        },
      ),
    });
  }

  getAuthority(form: AuthorityFormGroup): NewAuthority {
    return form.getRawValue() as NewAuthority;
  }

  resetForm(form: AuthorityFormGroup, authority: AuthorityFormGroupInput): void {
    const authorityRawValue = { ...this.getFormDefaults(), ...authority };
    form.reset(
      {
        ...authorityRawValue,
        name: { value: authorityRawValue.name, disabled: authorityRawValue.name !== null },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AuthorityFormDefaults {
    return {
      name: null,
    };
  }
}
