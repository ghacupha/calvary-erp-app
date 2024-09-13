import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInstitutionalSubscription } from '../institutional-subscription.model';
import { InstitutionalSubscriptionService } from '../service/institutional-subscription.service';

const institutionalSubscriptionResolve = (route: ActivatedRouteSnapshot): Observable<null | IInstitutionalSubscription> => {
  const id = route.params.id;
  if (id) {
    return inject(InstitutionalSubscriptionService)
      .find(id)
      .pipe(
        mergeMap((institutionalSubscription: HttpResponse<IInstitutionalSubscription>) => {
          if (institutionalSubscription.body) {
            return of(institutionalSubscription.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default institutionalSubscriptionResolve;
