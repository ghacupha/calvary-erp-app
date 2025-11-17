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

import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { SortOrder, SortState, SortStateSignal } from './sort-state';

export interface SortChangeDirective<T> {
  sortChange: EventEmitter<SortState>;

  sort(field: T): void;
}

@Directive({
  standalone: true,
  selector: '[jhiSort]',
})
export class SortDirective implements SortChangeDirective<string> {
  @Input() sortState!: SortStateSignal;

  @Output() sortChange = new EventEmitter<SortState>();

  sort(field: string): void {
    const { predicate, order } = this.sortState();
    const toggle = (): SortOrder => (order === 'asc' ? 'desc' : 'asc');
    this.sortChange.emit({ predicate: field, order: field !== predicate ? 'asc' : toggle() });
  }
}
