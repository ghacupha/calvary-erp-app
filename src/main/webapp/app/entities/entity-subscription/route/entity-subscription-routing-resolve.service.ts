import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEntitySubscription } from '../entity-subscription.model';
import { EntitySubscriptionService } from '../service/entity-subscription.service';

const entitySubscriptionResolve = (route: ActivatedRouteSnapshot): Observable<null | IEntitySubscription> => {
  const id = route.params.id;
  if (id) {
    return inject(EntitySubscriptionService)
      .find(id)
      .pipe(
        mergeMap((entitySubscription: HttpResponse<IEntitySubscription>) => {
          if (entitySubscription.body) {
            return of(entitySubscription.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default entitySubscriptionResolve;
