import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IEntitySubscription, NewEntitySubscription } from '../entity-subscription.model';

export type PartialUpdateEntitySubscription = Partial<IEntitySubscription> & Pick<IEntitySubscription, 'id'>;

type RestOf<T extends IEntitySubscription | NewEntitySubscription> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestEntitySubscription = RestOf<IEntitySubscription>;

export type NewRestEntitySubscription = RestOf<NewEntitySubscription>;

export type PartialUpdateRestEntitySubscription = RestOf<PartialUpdateEntitySubscription>;

export type EntityResponseType = HttpResponse<IEntitySubscription>;
export type EntityArrayResponseType = HttpResponse<IEntitySubscription[]>;

@Injectable({ providedIn: 'root' })
export class EntitySubscriptionService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/entity-subscriptions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/entity-subscriptions/_search');

  create(entitySubscription: NewEntitySubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(entitySubscription);
    return this.http
      .post<RestEntitySubscription>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(entitySubscription: IEntitySubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(entitySubscription);
    return this.http
      .put<RestEntitySubscription>(`${this.resourceUrl}/${this.getEntitySubscriptionIdentifier(entitySubscription)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(entitySubscription: PartialUpdateEntitySubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(entitySubscription);
    return this.http
      .patch<RestEntitySubscription>(`${this.resourceUrl}/${this.getEntitySubscriptionIdentifier(entitySubscription)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEntitySubscription>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEntitySubscription[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestEntitySubscription[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<IEntitySubscription[]>()], asapScheduler)),
    );
  }

  getEntitySubscriptionIdentifier(entitySubscription: Pick<IEntitySubscription, 'id'>): number {
    return entitySubscription.id;
  }

  compareEntitySubscription(o1: Pick<IEntitySubscription, 'id'> | null, o2: Pick<IEntitySubscription, 'id'> | null): boolean {
    return o1 && o2 ? this.getEntitySubscriptionIdentifier(o1) === this.getEntitySubscriptionIdentifier(o2) : o1 === o2;
  }

  addEntitySubscriptionToCollectionIfMissing<Type extends Pick<IEntitySubscription, 'id'>>(
    entitySubscriptionCollection: Type[],
    ...entitySubscriptionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const entitySubscriptions: Type[] = entitySubscriptionsToCheck.filter(isPresent);
    if (entitySubscriptions.length > 0) {
      const entitySubscriptionCollectionIdentifiers = entitySubscriptionCollection.map(entitySubscriptionItem =>
        this.getEntitySubscriptionIdentifier(entitySubscriptionItem),
      );
      const entitySubscriptionsToAdd = entitySubscriptions.filter(entitySubscriptionItem => {
        const entitySubscriptionIdentifier = this.getEntitySubscriptionIdentifier(entitySubscriptionItem);
        if (entitySubscriptionCollectionIdentifiers.includes(entitySubscriptionIdentifier)) {
          return false;
        }
        entitySubscriptionCollectionIdentifiers.push(entitySubscriptionIdentifier);
        return true;
      });
      return [...entitySubscriptionsToAdd, ...entitySubscriptionCollection];
    }
    return entitySubscriptionCollection;
  }

  protected convertDateFromClient<T extends IEntitySubscription | NewEntitySubscription | PartialUpdateEntitySubscription>(
    entitySubscription: T,
  ): RestOf<T> {
    return {
      ...entitySubscription,
      startDate: entitySubscription.startDate?.toJSON() ?? null,
      endDate: entitySubscription.endDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEntitySubscription: RestEntitySubscription): IEntitySubscription {
    return {
      ...restEntitySubscription,
      startDate: restEntitySubscription.startDate ? dayjs(restEntitySubscription.startDate) : undefined,
      endDate: restEntitySubscription.endDate ? dayjs(restEntitySubscription.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEntitySubscription>): HttpResponse<IEntitySubscription> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEntitySubscription[]>): HttpResponse<IEntitySubscription[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
