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

import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../entity-subscription.test-samples';

import { EntitySubscriptionFormService } from './entity-subscription-form.service';

describe('EntitySubscription Form Service', () => {
  let service: EntitySubscriptionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EntitySubscriptionFormService);
  });

  describe('Service methods', () => {
    describe('createEntitySubscriptionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEntitySubscriptionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subscriptionToken: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            institution: expect.any(Object),
          }),
        );
      });

      it('passing IEntitySubscription should create a new form with FormGroup', () => {
        const formGroup = service.createEntitySubscriptionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subscriptionToken: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            institution: expect.any(Object),
          }),
        );
      });
    });

    describe('getEntitySubscription', () => {
      it('should return NewEntitySubscription for default EntitySubscription initial value', () => {
        const formGroup = service.createEntitySubscriptionFormGroup(sampleWithNewData);

        const entitySubscription = service.getEntitySubscription(formGroup) as any;

        expect(entitySubscription).toMatchObject(sampleWithNewData);
      });

      it('should return NewEntitySubscription for empty EntitySubscription initial value', () => {
        const formGroup = service.createEntitySubscriptionFormGroup();

        const entitySubscription = service.getEntitySubscription(formGroup) as any;

        expect(entitySubscription).toMatchObject({});
      });

      it('should return IEntitySubscription', () => {
        const formGroup = service.createEntitySubscriptionFormGroup(sampleWithRequiredData);

        const entitySubscription = service.getEntitySubscription(formGroup) as any;

        expect(entitySubscription).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEntitySubscription should not enable id FormControl', () => {
        const formGroup = service.createEntitySubscriptionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEntitySubscription should disable id FormControl', () => {
        const formGroup = service.createEntitySubscriptionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
