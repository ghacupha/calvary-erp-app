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

import { Injectable } from '@angular/core';
import { SortState } from './sort-state';

@Injectable({ providedIn: 'root' })
export class SortService {
  private readonly collator = new Intl.Collator(undefined, {
    numeric: true,
    sensitivity: 'base',
  });

  public startSort({ predicate, order }: Required<SortState>, fallback?: Required<SortState>): (a: any, b: any) => number {
    const multiply = order === 'desc' ? -1 : 1;
    return (a: any, b: any) => {
      const compare = this.collator.compare(a[predicate], b[predicate]);
      if (compare === 0 && fallback) {
        return this.startSort(fallback)(a, b);
      }
      return compare * multiply;
    };
  }

  public parseSortParam(sortParam: string | undefined): SortState {
    if (sortParam?.includes(',')) {
      const split = sortParam.split(',');
      if (split[0]) {
        return { predicate: split[0], order: split[1] as any };
      }
    }
    return { predicate: sortParam?.length ? sortParam : undefined };
  }

  public buildSortParam({ predicate, order }: SortState, fallback?: string): string[] {
    const sortParam = predicate && order ? [`${predicate},${order}`] : [];
    if (fallback && predicate !== fallback) {
      sortParam.push(`${fallback},asc`);
    }
    return sortParam;
  }
}
