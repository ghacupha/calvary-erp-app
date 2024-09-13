import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../institutional-subscription.test-samples';

import { InstitutionalSubscriptionFormService } from './institutional-subscription-form.service';

describe('InstitutionalSubscription Form Service', () => {
  let service: InstitutionalSubscriptionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InstitutionalSubscriptionFormService);
  });

  describe('Service methods', () => {
    describe('createInstitutionalSubscriptionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInstitutionalSubscriptionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDate: expect.any(Object),
            expiryDate: expect.any(Object),
            memberLimit: expect.any(Object),
            institution: expect.any(Object),
          }),
        );
      });

      it('passing IInstitutionalSubscription should create a new form with FormGroup', () => {
        const formGroup = service.createInstitutionalSubscriptionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDate: expect.any(Object),
            expiryDate: expect.any(Object),
            memberLimit: expect.any(Object),
            institution: expect.any(Object),
          }),
        );
      });
    });

    describe('getInstitutionalSubscription', () => {
      it('should return NewInstitutionalSubscription for default InstitutionalSubscription initial value', () => {
        const formGroup = service.createInstitutionalSubscriptionFormGroup(sampleWithNewData);

        const institutionalSubscription = service.getInstitutionalSubscription(formGroup) as any;

        expect(institutionalSubscription).toMatchObject(sampleWithNewData);
      });

      it('should return NewInstitutionalSubscription for empty InstitutionalSubscription initial value', () => {
        const formGroup = service.createInstitutionalSubscriptionFormGroup();

        const institutionalSubscription = service.getInstitutionalSubscription(formGroup) as any;

        expect(institutionalSubscription).toMatchObject({});
      });

      it('should return IInstitutionalSubscription', () => {
        const formGroup = service.createInstitutionalSubscriptionFormGroup(sampleWithRequiredData);

        const institutionalSubscription = service.getInstitutionalSubscription(formGroup) as any;

        expect(institutionalSubscription).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInstitutionalSubscription should not enable id FormControl', () => {
        const formGroup = service.createInstitutionalSubscriptionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInstitutionalSubscription should disable id FormControl', () => {
        const formGroup = service.createInstitutionalSubscriptionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
