import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInstitution } from '../institution.model';
import { InstitutionService } from '../service/institution.service';

const institutionResolve = (route: ActivatedRouteSnapshot): Observable<null | IInstitution> => {
  const id = route.params.id;
  if (id) {
    return inject(InstitutionService)
      .find(id)
      .pipe(
        mergeMap((institution: HttpResponse<IInstitution>) => {
          if (institution.body) {
            return of(institution.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default institutionResolve;
