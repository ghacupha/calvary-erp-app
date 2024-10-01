import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEntitySubscription, NewEntitySubscription } from '../entity-subscription.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEntitySubscription for edit and NewEntitySubscriptionFormGroupInput for create.
 */
type EntitySubscriptionFormGroupInput = IEntitySubscription | PartialWithRequiredKeyOf<NewEntitySubscription>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEntitySubscription | NewEntitySubscription> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type EntitySubscriptionFormRawValue = FormValueOf<IEntitySubscription>;

type NewEntitySubscriptionFormRawValue = FormValueOf<NewEntitySubscription>;

type EntitySubscriptionFormDefaults = Pick<NewEntitySubscription, 'id' | 'startDate' | 'endDate'>;

type EntitySubscriptionFormGroupContent = {
  id: FormControl<EntitySubscriptionFormRawValue['id'] | NewEntitySubscription['id']>;
  subscriptionToken: FormControl<EntitySubscriptionFormRawValue['subscriptionToken']>;
  startDate: FormControl<EntitySubscriptionFormRawValue['startDate']>;
  endDate: FormControl<EntitySubscriptionFormRawValue['endDate']>;
  institution: FormControl<EntitySubscriptionFormRawValue['institution']>;
};

export type EntitySubscriptionFormGroup = FormGroup<EntitySubscriptionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EntitySubscriptionFormService {
  createEntitySubscriptionFormGroup(entitySubscription: EntitySubscriptionFormGroupInput = { id: null }): EntitySubscriptionFormGroup {
    const entitySubscriptionRawValue = this.convertEntitySubscriptionToEntitySubscriptionRawValue({
      ...this.getFormDefaults(),
      ...entitySubscription,
    });
    return new FormGroup<EntitySubscriptionFormGroupContent>({
      id: new FormControl(
        { value: entitySubscriptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      subscriptionToken: new FormControl(entitySubscriptionRawValue.subscriptionToken, {
        validators: [Validators.required],
      }),
      startDate: new FormControl(entitySubscriptionRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(entitySubscriptionRawValue.endDate, {
        validators: [Validators.required],
      }),
      institution: new FormControl(entitySubscriptionRawValue.institution),
    });
  }

  getEntitySubscription(form: EntitySubscriptionFormGroup): IEntitySubscription | NewEntitySubscription {
    return this.convertEntitySubscriptionRawValueToEntitySubscription(
      form.getRawValue() as EntitySubscriptionFormRawValue | NewEntitySubscriptionFormRawValue,
    );
  }

  resetForm(form: EntitySubscriptionFormGroup, entitySubscription: EntitySubscriptionFormGroupInput): void {
    const entitySubscriptionRawValue = this.convertEntitySubscriptionToEntitySubscriptionRawValue({
      ...this.getFormDefaults(),
      ...entitySubscription,
    });
    form.reset(
      {
        ...entitySubscriptionRawValue,
        id: { value: entitySubscriptionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EntitySubscriptionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertEntitySubscriptionRawValueToEntitySubscription(
    rawEntitySubscription: EntitySubscriptionFormRawValue | NewEntitySubscriptionFormRawValue,
  ): IEntitySubscription | NewEntitySubscription {
    return {
      ...rawEntitySubscription,
      startDate: dayjs(rawEntitySubscription.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawEntitySubscription.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertEntitySubscriptionToEntitySubscriptionRawValue(
    entitySubscription: IEntitySubscription | (Partial<NewEntitySubscription> & EntitySubscriptionFormDefaults),
  ): EntitySubscriptionFormRawValue | PartialWithRequiredKeyOf<NewEntitySubscriptionFormRawValue> {
    return {
      ...entitySubscription,
      startDate: entitySubscription.startDate ? entitySubscription.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: entitySubscription.endDate ? entitySubscription.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
