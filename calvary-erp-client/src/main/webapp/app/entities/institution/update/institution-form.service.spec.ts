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

import { sampleWithNewData, sampleWithRequiredData } from '../institution.test-samples';

import { InstitutionFormService } from './institution-form.service';

describe('Institution Form Service', () => {
  let service: InstitutionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InstitutionFormService);
  });

  describe('Service methods', () => {
    describe('createInstitutionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInstitutionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          }),
        );
      });

      it('passing IInstitution should create a new form with FormGroup', () => {
        const formGroup = service.createInstitutionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          }),
        );
      });
    });

    describe('getInstitution', () => {
      it('should return NewInstitution for default Institution initial value', () => {
        const formGroup = service.createInstitutionFormGroup(sampleWithNewData);

        const institution = service.getInstitution(formGroup) as any;

        expect(institution).toMatchObject(sampleWithNewData);
      });

      it('should return NewInstitution for empty Institution initial value', () => {
        const formGroup = service.createInstitutionFormGroup();

        const institution = service.getInstitution(formGroup) as any;

        expect(institution).toMatchObject({});
      });

      it('should return IInstitution', () => {
        const formGroup = service.createInstitutionFormGroup(sampleWithRequiredData);

        const institution = service.getInstitution(formGroup) as any;

        expect(institution).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInstitution should not enable id FormControl', () => {
        const formGroup = service.createInstitutionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInstitution should disable id FormControl', () => {
        const formGroup = service.createInstitutionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
