import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import InstitutionResolve from './route/institution-routing-resolve.service';

const institutionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/institution.component').then(m => m.InstitutionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/institution-detail.component').then(m => m.InstitutionDetailComponent),
    resolve: {
      institution: InstitutionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/institution-update.component').then(m => m.InstitutionUpdateComponent),
    resolve: {
      institution: InstitutionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/institution-update.component').then(m => m.InstitutionUpdateComponent),
    resolve: {
      institution: InstitutionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default institutionRoute;
