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

import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEntitySubscription } from '../entity-subscription.model';
import { EntitySubscriptionService } from '../../../../entities/entity-subscription/service/entity-subscription.service';

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
