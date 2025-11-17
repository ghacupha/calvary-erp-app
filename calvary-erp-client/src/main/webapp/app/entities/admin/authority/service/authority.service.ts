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

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAuthority, NewAuthority } from '../authority.model';

export type EntityResponseType = HttpResponse<IAuthority>;
export type EntityArrayResponseType = HttpResponse<IAuthority[]>;

@Injectable({ providedIn: 'root' })
export class AuthorityService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/authorities');

  create(authority: NewAuthority): Observable<EntityResponseType> {
    return this.http.post<IAuthority>(this.resourceUrl, authority, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IAuthority>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAuthority[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAuthorityIdentifier(authority: Pick<IAuthority, 'name'>): string {
    return authority.name;
  }

  compareAuthority(o1: Pick<IAuthority, 'name'> | null, o2: Pick<IAuthority, 'name'> | null): boolean {
    return o1 && o2 ? this.getAuthorityIdentifier(o1) === this.getAuthorityIdentifier(o2) : o1 === o2;
  }

  addAuthorityToCollectionIfMissing<Type extends Pick<IAuthority, 'name'>>(
    authorityCollection: Type[],
    ...authoritiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const authorities: Type[] = authoritiesToCheck.filter(isPresent);
    if (authorities.length > 0) {
      const authorityCollectionIdentifiers = authorityCollection.map(authorityItem => this.getAuthorityIdentifier(authorityItem));
      const authoritiesToAdd = authorities.filter(authorityItem => {
        const authorityIdentifier = this.getAuthorityIdentifier(authorityItem);
        if (authorityCollectionIdentifiers.includes(authorityIdentifier)) {
          return false;
        }
        authorityCollectionIdentifiers.push(authorityIdentifier);
        return true;
      });
      return [...authoritiesToAdd, ...authorityCollection];
    }
    return authorityCollection;
  }
}
