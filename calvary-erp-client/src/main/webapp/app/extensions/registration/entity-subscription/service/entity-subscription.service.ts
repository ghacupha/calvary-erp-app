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

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IEntitySubscription, NewEntitySubscription } from '../entity-subscription.model';
import { EntitySubscriptionService } from '../../../../entities/entity-subscription/service/entity-subscription.service';

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
export class ERPEntitySubscriptionService extends EntitySubscriptionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected override resourceUrl = this.applicationConfigService.getEndpointFor('api/erp/entity-subscriptions');
  protected override resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/erp/entity-subscriptions/_search');
}
