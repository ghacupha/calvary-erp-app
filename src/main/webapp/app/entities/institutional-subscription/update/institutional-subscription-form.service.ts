import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IInstitutionalSubscription, NewInstitutionalSubscription } from '../institutional-subscription.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInstitutionalSubscription for edit and NewInstitutionalSubscriptionFormGroupInput for create.
 */
type InstitutionalSubscriptionFormGroupInput = IInstitutionalSubscription | PartialWithRequiredKeyOf<NewInstitutionalSubscription>;

type InstitutionalSubscriptionFormDefaults = Pick<NewInstitutionalSubscription, 'id'>;

type InstitutionalSubscriptionFormGroupContent = {
  id: FormControl<IInstitutionalSubscription['id'] | NewInstitutionalSubscription['id']>;
  startDate: FormControl<IInstitutionalSubscription['startDate']>;
  expiryDate: FormControl<IInstitutionalSubscription['expiryDate']>;
  memberLimit: FormControl<IInstitutionalSubscription['memberLimit']>;
  institution: FormControl<IInstitutionalSubscription['institution']>;
};

export type InstitutionalSubscriptionFormGroup = FormGroup<InstitutionalSubscriptionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InstitutionalSubscriptionFormService {
  createInstitutionalSubscriptionFormGroup(
    institutionalSubscription: InstitutionalSubscriptionFormGroupInput = { id: null },
  ): InstitutionalSubscriptionFormGroup {
    const institutionalSubscriptionRawValue = {
      ...this.getFormDefaults(),
      ...institutionalSubscription,
    };
    return new FormGroup<InstitutionalSubscriptionFormGroupContent>({
      id: new FormControl(
        { value: institutionalSubscriptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      startDate: new FormControl(institutionalSubscriptionRawValue.startDate, {
        validators: [Validators.required],
      }),
      expiryDate: new FormControl(institutionalSubscriptionRawValue.expiryDate, {
        validators: [Validators.required],
      }),
      memberLimit: new FormControl(institutionalSubscriptionRawValue.memberLimit, {
        validators: [Validators.required],
      }),
      institution: new FormControl(institutionalSubscriptionRawValue.institution, {
        validators: [Validators.required],
      }),
    });
  }

  getInstitutionalSubscription(form: InstitutionalSubscriptionFormGroup): IInstitutionalSubscription | NewInstitutionalSubscription {
    return form.getRawValue() as IInstitutionalSubscription | NewInstitutionalSubscription;
  }

  resetForm(form: InstitutionalSubscriptionFormGroup, institutionalSubscription: InstitutionalSubscriptionFormGroupInput): void {
    const institutionalSubscriptionRawValue = { ...this.getFormDefaults(), ...institutionalSubscription };
    form.reset(
      {
        ...institutionalSubscriptionRawValue,
        id: { value: institutionalSubscriptionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): InstitutionalSubscriptionFormDefaults {
    return {
      id: null,
    };
  }
}
