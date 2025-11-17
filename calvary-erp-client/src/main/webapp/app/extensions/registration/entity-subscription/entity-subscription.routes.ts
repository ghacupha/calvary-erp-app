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

import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import EntitySubscriptionResolve from './route/entity-subscription-routing-resolve.service';

const entitySubscriptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/entity-subscription.component').then(m => m.EntitySubscriptionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/entity-subscription-detail.component').then(m => m.EntitySubscriptionDetailComponent),
    resolve: {
      entitySubscription: EntitySubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/entity-subscription-update.component').then(m => m.EntitySubscriptionUpdateComponent),
    resolve: {
      entitySubscription: EntitySubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/entity-subscription-update.component').then(m => m.EntitySubscriptionUpdateComponent),
    resolve: {
      entitySubscription: EntitySubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default entitySubscriptionRoute;
