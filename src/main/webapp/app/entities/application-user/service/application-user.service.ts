import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IApplicationUser, NewApplicationUser } from '../application-user.model';

export type PartialUpdateApplicationUser = Partial<IApplicationUser> & Pick<IApplicationUser, 'id'>;

type RestOf<T extends IApplicationUser | NewApplicationUser> = Omit<T, 'resetDate'> & {
  resetDate?: string | null;
};

export type RestApplicationUser = RestOf<IApplicationUser>;

export type NewRestApplicationUser = RestOf<NewApplicationUser>;

export type PartialUpdateRestApplicationUser = RestOf<PartialUpdateApplicationUser>;

export type EntityResponseType = HttpResponse<IApplicationUser>;
export type EntityArrayResponseType = HttpResponse<IApplicationUser[]>;

@Injectable({ providedIn: 'root' })
export class ApplicationUserService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/application-users');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/application-users/_search');

  create(applicationUser: NewApplicationUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(applicationUser);
    return this.http
      .post<RestApplicationUser>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(applicationUser: IApplicationUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(applicationUser);
    return this.http
      .put<RestApplicationUser>(`${this.resourceUrl}/${this.getApplicationUserIdentifier(applicationUser)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(applicationUser: PartialUpdateApplicationUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(applicationUser);
    return this.http
      .patch<RestApplicationUser>(`${this.resourceUrl}/${this.getApplicationUserIdentifier(applicationUser)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestApplicationUser>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestApplicationUser[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestApplicationUser[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<IApplicationUser[]>()], asapScheduler)),
    );
  }

  getApplicationUserIdentifier(applicationUser: Pick<IApplicationUser, 'id'>): number {
    return applicationUser.id;
  }

  compareApplicationUser(o1: Pick<IApplicationUser, 'id'> | null, o2: Pick<IApplicationUser, 'id'> | null): boolean {
    return o1 && o2 ? this.getApplicationUserIdentifier(o1) === this.getApplicationUserIdentifier(o2) : o1 === o2;
  }

  addApplicationUserToCollectionIfMissing<Type extends Pick<IApplicationUser, 'id'>>(
    applicationUserCollection: Type[],
    ...applicationUsersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const applicationUsers: Type[] = applicationUsersToCheck.filter(isPresent);
    if (applicationUsers.length > 0) {
      const applicationUserCollectionIdentifiers = applicationUserCollection.map(applicationUserItem =>
        this.getApplicationUserIdentifier(applicationUserItem),
      );
      const applicationUsersToAdd = applicationUsers.filter(applicationUserItem => {
        const applicationUserIdentifier = this.getApplicationUserIdentifier(applicationUserItem);
        if (applicationUserCollectionIdentifiers.includes(applicationUserIdentifier)) {
          return false;
        }
        applicationUserCollectionIdentifiers.push(applicationUserIdentifier);
        return true;
      });
      return [...applicationUsersToAdd, ...applicationUserCollection];
    }
    return applicationUserCollection;
  }

  protected convertDateFromClient<T extends IApplicationUser | NewApplicationUser | PartialUpdateApplicationUser>(
    applicationUser: T,
  ): RestOf<T> {
    return {
      ...applicationUser,
      resetDate: applicationUser.resetDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restApplicationUser: RestApplicationUser): IApplicationUser {
    return {
      ...restApplicationUser,
      resetDate: restApplicationUser.resetDate ? dayjs(restApplicationUser.resetDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestApplicationUser>): HttpResponse<IApplicationUser> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestApplicationUser[]>): HttpResponse<IApplicationUser[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
