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
  institutionName: FormControl<IInstitution['institutionName']>;
  parentInstitution: FormControl<IInstitution['parentInstitution']>;
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
      institutionName: new FormControl(institutionRawValue.institutionName, {
        validators: [Validators.required],
      }),
      parentInstitution: new FormControl(institutionRawValue.parentInstitution),
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
