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

import { sampleWithNewData, sampleWithRequiredData } from '../authority.test-samples';

import { AuthorityFormService } from './authority-form.service';

describe('Authority Form Service', () => {
  let service: AuthorityFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthorityFormService);
  });

  describe('Service methods', () => {
    describe('createAuthorityFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAuthorityFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            name: expect.any(Object),
          }),
        );
      });

      it('passing IAuthority should create a new form with FormGroup', () => {
        const formGroup = service.createAuthorityFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            name: expect.any(Object),
          }),
        );
      });
    });

    describe('getAuthority', () => {
      it('should return NewAuthority for default Authority initial value', () => {
        const formGroup = service.createAuthorityFormGroup(sampleWithNewData);

        const authority = service.getAuthority(formGroup) as any;

        expect(authority).toMatchObject(sampleWithNewData);
      });

      it('should return NewAuthority for empty Authority initial value', () => {
        const formGroup = service.createAuthorityFormGroup();

        const authority = service.getAuthority(formGroup) as any;

        expect(authority).toMatchObject({});
      });

      it('should return IAuthority', () => {
        const formGroup = service.createAuthorityFormGroup(sampleWithRequiredData);

        const authority = service.getAuthority(formGroup) as any;

        expect(authority).toMatchObject(sampleWithRequiredData);
      });
    });
  });
});
