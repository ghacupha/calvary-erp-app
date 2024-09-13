import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import InstitutionalSubscriptionResolve from './route/institutional-subscription-routing-resolve.service';

const institutionalSubscriptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/institutional-subscription.component').then(m => m.InstitutionalSubscriptionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () =>
      import('./detail/institutional-subscription-detail.component').then(m => m.InstitutionalSubscriptionDetailComponent),
    resolve: {
      institutionalSubscription: InstitutionalSubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./update/institutional-subscription-update.component').then(m => m.InstitutionalSubscriptionUpdateComponent),
    resolve: {
      institutionalSubscription: InstitutionalSubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./update/institutional-subscription-update.component').then(m => m.InstitutionalSubscriptionUpdateComponent),
    resolve: {
      institutionalSubscription: InstitutionalSubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default institutionalSubscriptionRoute;
