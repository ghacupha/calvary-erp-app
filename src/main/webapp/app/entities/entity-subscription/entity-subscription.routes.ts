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
