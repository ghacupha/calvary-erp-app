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
import { IApplicationUser, NewApplicationUser } from '../application-user.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';

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
export class ERPApplicationUserService extends ApplicationUserService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected override resourceUrl = this.applicationConfigService.getEndpointFor('api/erp/application-users');
  protected override resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/erp/application-users/_search');
}
