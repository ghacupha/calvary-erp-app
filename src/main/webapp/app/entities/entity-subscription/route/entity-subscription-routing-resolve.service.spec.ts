import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IEntitySubscription } from '../entity-subscription.model';
import { EntitySubscriptionService } from '../service/entity-subscription.service';

import entitySubscriptionResolve from './entity-subscription-routing-resolve.service';

describe('EntitySubscription routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: EntitySubscriptionService;
  let resultEntitySubscription: IEntitySubscription | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(EntitySubscriptionService);
    resultEntitySubscription = undefined;
  });

  describe('resolve', () => {
    it('should return IEntitySubscription returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        entitySubscriptionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultEntitySubscription = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultEntitySubscription).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        entitySubscriptionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultEntitySubscription = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultEntitySubscription).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IEntitySubscription>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        entitySubscriptionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultEntitySubscription = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultEntitySubscription).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
