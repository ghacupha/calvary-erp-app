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

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IApplicationUser, NewApplicationUser } from '../application-user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IApplicationUser for edit and NewApplicationUserFormGroupInput for create.
 */
type ApplicationUserFormGroupInput = IApplicationUser | PartialWithRequiredKeyOf<NewApplicationUser>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IApplicationUser | NewApplicationUser> = Omit<T, 'resetDate'> & {
  resetDate?: string | null;
};

type ApplicationUserFormRawValue = FormValueOf<IApplicationUser>;

type NewApplicationUserFormRawValue = FormValueOf<NewApplicationUser>;

type ApplicationUserFormDefaults = Pick<NewApplicationUser, 'id' | 'activated' | 'resetDate'>;

type ApplicationUserFormGroupContent = {
  id: FormControl<ApplicationUserFormRawValue['id'] | NewApplicationUser['id']>;
  username: FormControl<ApplicationUserFormRawValue['username']>;
  firstName: FormControl<ApplicationUserFormRawValue['firstName']>;
  lastName: FormControl<ApplicationUserFormRawValue['lastName']>;
  email: FormControl<ApplicationUserFormRawValue['email']>;
  activated: FormControl<ApplicationUserFormRawValue['activated']>;
  langKey: FormControl<ApplicationUserFormRawValue['langKey']>;
  imageUrl: FormControl<ApplicationUserFormRawValue['imageUrl']>;
  activationKey: FormControl<ApplicationUserFormRawValue['activationKey']>;
  resetKey: FormControl<ApplicationUserFormRawValue['resetKey']>;
  resetDate: FormControl<ApplicationUserFormRawValue['resetDate']>;
  systemUser: FormControl<ApplicationUserFormRawValue['systemUser']>;
  institution: FormControl<ApplicationUserFormRawValue['institution']>;
};

export type ApplicationUserFormGroup = FormGroup<ApplicationUserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ApplicationUserFormService {
  createApplicationUserFormGroup(applicationUser: ApplicationUserFormGroupInput = { id: null }): ApplicationUserFormGroup {
    const applicationUserRawValue = this.convertApplicationUserToApplicationUserRawValue({
      ...this.getFormDefaults(),
      ...applicationUser,
    });
    return new FormGroup<ApplicationUserFormGroupContent>({
      id: new FormControl(
        { value: applicationUserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      username: new FormControl(applicationUserRawValue.username, {
        validators: [Validators.required],
      }),
      firstName: new FormControl(applicationUserRawValue.firstName),
      lastName: new FormControl(applicationUserRawValue.lastName),
      email: new FormControl(applicationUserRawValue.email),
      activated: new FormControl(applicationUserRawValue.activated),
      langKey: new FormControl(applicationUserRawValue.langKey),
      imageUrl: new FormControl(applicationUserRawValue.imageUrl),
      activationKey: new FormControl(applicationUserRawValue.activationKey),
      resetKey: new FormControl(applicationUserRawValue.resetKey),
      resetDate: new FormControl(applicationUserRawValue.resetDate),
      systemUser: new FormControl(applicationUserRawValue.systemUser, {
        validators: [Validators.required],
      }),
      institution: new FormControl(applicationUserRawValue.institution, {
        validators: [Validators.required],
      }),
    });
  }

  getApplicationUser(form: ApplicationUserFormGroup): IApplicationUser | NewApplicationUser {
    return this.convertApplicationUserRawValueToApplicationUser(
      form.getRawValue() as ApplicationUserFormRawValue | NewApplicationUserFormRawValue,
    );
  }

  resetForm(form: ApplicationUserFormGroup, applicationUser: ApplicationUserFormGroupInput): void {
    const applicationUserRawValue = this.convertApplicationUserToApplicationUserRawValue({ ...this.getFormDefaults(), ...applicationUser });
    form.reset(
      {
        ...applicationUserRawValue,
        id: { value: applicationUserRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ApplicationUserFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      activated: false,
      resetDate: currentTime,
    };
  }

  private convertApplicationUserRawValueToApplicationUser(
    rawApplicationUser: ApplicationUserFormRawValue | NewApplicationUserFormRawValue,
  ): IApplicationUser | NewApplicationUser {
    return {
      ...rawApplicationUser,
      resetDate: dayjs(rawApplicationUser.resetDate, DATE_TIME_FORMAT),
    };
  }

  private convertApplicationUserToApplicationUserRawValue(
    applicationUser: IApplicationUser | (Partial<NewApplicationUser> & ApplicationUserFormDefaults),
  ): ApplicationUserFormRawValue | PartialWithRequiredKeyOf<NewApplicationUserFormRawValue> {
    return {
      ...applicationUser,
      resetDate: applicationUser.resetDate ? applicationUser.resetDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
