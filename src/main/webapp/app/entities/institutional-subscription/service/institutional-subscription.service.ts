import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IInstitutionalSubscription, NewInstitutionalSubscription } from '../institutional-subscription.model';

export type PartialUpdateInstitutionalSubscription = Partial<IInstitutionalSubscription> & Pick<IInstitutionalSubscription, 'id'>;

type RestOf<T extends IInstitutionalSubscription | NewInstitutionalSubscription> = Omit<T, 'startDate' | 'expiryDate'> & {
  startDate?: string | null;
  expiryDate?: string | null;
};

export type RestInstitutionalSubscription = RestOf<IInstitutionalSubscription>;

export type NewRestInstitutionalSubscription = RestOf<NewInstitutionalSubscription>;

export type PartialUpdateRestInstitutionalSubscription = RestOf<PartialUpdateInstitutionalSubscription>;

export type EntityResponseType = HttpResponse<IInstitutionalSubscription>;
export type EntityArrayResponseType = HttpResponse<IInstitutionalSubscription[]>;

@Injectable({ providedIn: 'root' })
export class InstitutionalSubscriptionService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/institutional-subscriptions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/institutional-subscriptions/_search');

  create(institutionalSubscription: NewInstitutionalSubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(institutionalSubscription);
    return this.http
      .post<RestInstitutionalSubscription>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(institutionalSubscription: IInstitutionalSubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(institutionalSubscription);
    return this.http
      .put<RestInstitutionalSubscription>(
        `${this.resourceUrl}/${this.getInstitutionalSubscriptionIdentifier(institutionalSubscription)}`,
        copy,
        { observe: 'response' },
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(institutionalSubscription: PartialUpdateInstitutionalSubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(institutionalSubscription);
    return this.http
      .patch<RestInstitutionalSubscription>(
        `${this.resourceUrl}/${this.getInstitutionalSubscriptionIdentifier(institutionalSubscription)}`,
        copy,
        { observe: 'response' },
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestInstitutionalSubscription>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestInstitutionalSubscription[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestInstitutionalSubscription[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<IInstitutionalSubscription[]>()], asapScheduler)),
    );
  }

  getInstitutionalSubscriptionIdentifier(institutionalSubscription: Pick<IInstitutionalSubscription, 'id'>): number {
    return institutionalSubscription.id;
  }

  compareInstitutionalSubscription(
    o1: Pick<IInstitutionalSubscription, 'id'> | null,
    o2: Pick<IInstitutionalSubscription, 'id'> | null,
  ): boolean {
    return o1 && o2 ? this.getInstitutionalSubscriptionIdentifier(o1) === this.getInstitutionalSubscriptionIdentifier(o2) : o1 === o2;
  }

  addInstitutionalSubscriptionToCollectionIfMissing<Type extends Pick<IInstitutionalSubscription, 'id'>>(
    institutionalSubscriptionCollection: Type[],
    ...institutionalSubscriptionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const institutionalSubscriptions: Type[] = institutionalSubscriptionsToCheck.filter(isPresent);
    if (institutionalSubscriptions.length > 0) {
      const institutionalSubscriptionCollectionIdentifiers = institutionalSubscriptionCollection.map(institutionalSubscriptionItem =>
        this.getInstitutionalSubscriptionIdentifier(institutionalSubscriptionItem),
      );
      const institutionalSubscriptionsToAdd = institutionalSubscriptions.filter(institutionalSubscriptionItem => {
        const institutionalSubscriptionIdentifier = this.getInstitutionalSubscriptionIdentifier(institutionalSubscriptionItem);
        if (institutionalSubscriptionCollectionIdentifiers.includes(institutionalSubscriptionIdentifier)) {
          return false;
        }
        institutionalSubscriptionCollectionIdentifiers.push(institutionalSubscriptionIdentifier);
        return true;
      });
      return [...institutionalSubscriptionsToAdd, ...institutionalSubscriptionCollection];
    }
    return institutionalSubscriptionCollection;
  }

  protected convertDateFromClient<
    T extends IInstitutionalSubscription | NewInstitutionalSubscription | PartialUpdateInstitutionalSubscription,
  >(institutionalSubscription: T): RestOf<T> {
    return {
      ...institutionalSubscription,
      startDate: institutionalSubscription.startDate?.format(DATE_FORMAT) ?? null,
      expiryDate: institutionalSubscription.expiryDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restInstitutionalSubscription: RestInstitutionalSubscription): IInstitutionalSubscription {
    return {
      ...restInstitutionalSubscription,
      startDate: restInstitutionalSubscription.startDate ? dayjs(restInstitutionalSubscription.startDate) : undefined,
      expiryDate: restInstitutionalSubscription.expiryDate ? dayjs(restInstitutionalSubscription.expiryDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestInstitutionalSubscription>): HttpResponse<IInstitutionalSubscription> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestInstitutionalSubscription[]>): HttpResponse<IInstitutionalSubscription[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
