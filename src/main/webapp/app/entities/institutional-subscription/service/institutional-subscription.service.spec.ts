import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IInstitutionalSubscription } from '../institutional-subscription.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../institutional-subscription.test-samples';

import { InstitutionalSubscriptionService, RestInstitutionalSubscription } from './institutional-subscription.service';

const requireRestSample: RestInstitutionalSubscription = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.format(DATE_FORMAT),
  expiryDate: sampleWithRequiredData.expiryDate?.format(DATE_FORMAT),
};

describe('InstitutionalSubscription Service', () => {
  let service: InstitutionalSubscriptionService;
  let httpMock: HttpTestingController;
  let expectedResult: IInstitutionalSubscription | IInstitutionalSubscription[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(InstitutionalSubscriptionService);
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

    it('should create a InstitutionalSubscription', () => {
      const institutionalSubscription = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(institutionalSubscription).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a InstitutionalSubscription', () => {
      const institutionalSubscription = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(institutionalSubscription).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a InstitutionalSubscription', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of InstitutionalSubscription', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a InstitutionalSubscription', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a InstitutionalSubscription', () => {
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

    describe('addInstitutionalSubscriptionToCollectionIfMissing', () => {
      it('should add a InstitutionalSubscription to an empty array', () => {
        const institutionalSubscription: IInstitutionalSubscription = sampleWithRequiredData;
        expectedResult = service.addInstitutionalSubscriptionToCollectionIfMissing([], institutionalSubscription);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(institutionalSubscription);
      });

      it('should not add a InstitutionalSubscription to an array that contains it', () => {
        const institutionalSubscription: IInstitutionalSubscription = sampleWithRequiredData;
        const institutionalSubscriptionCollection: IInstitutionalSubscription[] = [
          {
            ...institutionalSubscription,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addInstitutionalSubscriptionToCollectionIfMissing(
          institutionalSubscriptionCollection,
          institutionalSubscription,
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a InstitutionalSubscription to an array that doesn't contain it", () => {
        const institutionalSubscription: IInstitutionalSubscription = sampleWithRequiredData;
        const institutionalSubscriptionCollection: IInstitutionalSubscription[] = [sampleWithPartialData];
        expectedResult = service.addInstitutionalSubscriptionToCollectionIfMissing(
          institutionalSubscriptionCollection,
          institutionalSubscription,
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(institutionalSubscription);
      });

      it('should add only unique InstitutionalSubscription to an array', () => {
        const institutionalSubscriptionArray: IInstitutionalSubscription[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const institutionalSubscriptionCollection: IInstitutionalSubscription[] = [sampleWithRequiredData];
        expectedResult = service.addInstitutionalSubscriptionToCollectionIfMissing(
          institutionalSubscriptionCollection,
          ...institutionalSubscriptionArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const institutionalSubscription: IInstitutionalSubscription = sampleWithRequiredData;
        const institutionalSubscription2: IInstitutionalSubscription = sampleWithPartialData;
        expectedResult = service.addInstitutionalSubscriptionToCollectionIfMissing(
          [],
          institutionalSubscription,
          institutionalSubscription2,
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(institutionalSubscription);
        expect(expectedResult).toContain(institutionalSubscription2);
      });

      it('should accept null and undefined values', () => {
        const institutionalSubscription: IInstitutionalSubscription = sampleWithRequiredData;
        expectedResult = service.addInstitutionalSubscriptionToCollectionIfMissing([], null, institutionalSubscription, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(institutionalSubscription);
      });

      it('should return initial array if no InstitutionalSubscription is added', () => {
        const institutionalSubscriptionCollection: IInstitutionalSubscription[] = [sampleWithRequiredData];
        expectedResult = service.addInstitutionalSubscriptionToCollectionIfMissing(institutionalSubscriptionCollection, undefined, null);
        expect(expectedResult).toEqual(institutionalSubscriptionCollection);
      });
    });

    describe('compareInstitutionalSubscription', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareInstitutionalSubscription(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareInstitutionalSubscription(entity1, entity2);
        const compareResult2 = service.compareInstitutionalSubscription(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareInstitutionalSubscription(entity1, entity2);
        const compareResult2 = service.compareInstitutionalSubscription(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareInstitutionalSubscription(entity1, entity2);
        const compareResult2 = service.compareInstitutionalSubscription(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
