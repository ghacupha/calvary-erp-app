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
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IApplicationUser } from '../application-user.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../application-user.test-samples';

import { ApplicationUserService, RestApplicationUser } from './application-user.service';

const requireRestSample: RestApplicationUser = {
  ...sampleWithRequiredData,
  resetDate: sampleWithRequiredData.resetDate?.toJSON(),
};

describe('ApplicationUser Service', () => {
  let service: ApplicationUserService;
  let httpMock: HttpTestingController;
  let expectedResult: IApplicationUser | IApplicationUser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ApplicationUserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ApplicationUser', () => {
      const applicationUser = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(applicationUser).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ApplicationUser', () => {
      const applicationUser = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(applicationUser).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ApplicationUser', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ApplicationUser', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ApplicationUser', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a ApplicationUser', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addApplicationUserToCollectionIfMissing', () => {
      it('should add a ApplicationUser to an empty array', () => {
        const applicationUser: IApplicationUser = sampleWithRequiredData;
        expectedResult = service.addApplicationUserToCollectionIfMissing([], applicationUser);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(applicationUser);
      });

      it('should not add a ApplicationUser to an array that contains it', () => {
        const applicationUser: IApplicationUser = sampleWithRequiredData;
        const applicationUserCollection: IApplicationUser[] = [
          {
            ...applicationUser,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addApplicationUserToCollectionIfMissing(applicationUserCollection, applicationUser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ApplicationUser to an array that doesn't contain it", () => {
        const applicationUser: IApplicationUser = sampleWithRequiredData;
        const applicationUserCollection: IApplicationUser[] = [sampleWithPartialData];
        expectedResult = service.addApplicationUserToCollectionIfMissing(applicationUserCollection, applicationUser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(applicationUser);
      });

      it('should add only unique ApplicationUser to an array', () => {
        const applicationUserArray: IApplicationUser[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const applicationUserCollection: IApplicationUser[] = [sampleWithRequiredData];
        expectedResult = service.addApplicationUserToCollectionIfMissing(applicationUserCollection, ...applicationUserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const applicationUser: IApplicationUser = sampleWithRequiredData;
        const applicationUser2: IApplicationUser = sampleWithPartialData;
        expectedResult = service.addApplicationUserToCollectionIfMissing([], applicationUser, applicationUser2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(applicationUser);
        expect(expectedResult).toContain(applicationUser2);
      });

      it('should accept null and undefined values', () => {
        const applicationUser: IApplicationUser = sampleWithRequiredData;
        expectedResult = service.addApplicationUserToCollectionIfMissing([], null, applicationUser, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(applicationUser);
      });

      it('should return initial array if no ApplicationUser is added', () => {
        const applicationUserCollection: IApplicationUser[] = [sampleWithRequiredData];
        expectedResult = service.addApplicationUserToCollectionIfMissing(applicationUserCollection, undefined, null);
        expect(expectedResult).toEqual(applicationUserCollection);
      });
    });

    describe('compareApplicationUser', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareApplicationUser(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareApplicationUser(entity1, entity2);
        const compareResult2 = service.compareApplicationUser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareApplicationUser(entity1, entity2);
        const compareResult2 = service.compareApplicationUser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareApplicationUser(entity1, entity2);
        const compareResult2 = service.compareApplicationUser(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
