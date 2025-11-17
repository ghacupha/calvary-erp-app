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
import { ActivatedRouteSnapshot, ResolveFn, Routes } from '@angular/router';
import { of } from 'rxjs';

import { IUser } from './user-management.model';
import { UserManagementService } from './service/user-management.service';

export const userManagementResolve: ResolveFn<IUser | null> = (route: ActivatedRouteSnapshot) => {
  const login = route.paramMap.get('login');
  if (login) {
    return inject(UserManagementService).find(login);
  }
  return of(null);
};

const userManagementRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user-management.component'),
    data: {
      defaultSort: 'id,asc',
    },
  },
  {
    path: ':login/view',
    loadComponent: () => import('./detail/user-management-detail.component'),
    resolve: {
      user: userManagementResolve,
    },
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-management-update.component'),
    resolve: {
      user: userManagementResolve,
    },
  },
  {
    path: ':login/edit',
    loadComponent: () => import('./update/user-management-update.component'),
    resolve: {
      user: userManagementResolve,
    },
  },
];

export default userManagementRoute;
