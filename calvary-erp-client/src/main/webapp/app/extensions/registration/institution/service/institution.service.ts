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
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInstitution } from '../institution.model';
import { InstitutionService } from '../../../../entities/institution/service/institution.service';
import { ASC, DESC } from '../../../../config/navigation.constants';

export type PartialUpdateInstitution = Partial<IInstitution> & Pick<IInstitution, 'id'>;

export type EntityResponseType = HttpResponse<IInstitution>;
export type EntityArrayResponseType = HttpResponse<IInstitution[]>;

@Injectable({ providedIn: 'root' })
export class ERPInstitutionService extends InstitutionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected override resourceUrl = this.applicationConfigService.getEndpointFor('api/erp/institutions');
  protected override resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/erp/institutions/_search');
  protected resourceRegisteredUrl = this.applicationConfigService.getEndpointFor('api/erp/institutions/registered');

  queryRegistered(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInstitution[]>(this.resourceRegisteredUrl, { params: options, observe: 'response' });
  }

  searchInputs(searchText: string): Observable<IInstitution[]> {
    const options = { query: searchText, page: 0, size: 100, sort: this.sort() };

    return this.http.get<IInstitution[]>(this.resourceSearchUrl, { params: options });
  }

  sort(): string[] {
    const predicate = 'id';
    const ascending = false;

    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    const result = [predicate + ',' + (ascending ? ASC : DESC)];
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (predicate !== 'id') {
      result.push('id');
    }
    return result;
  }
}
