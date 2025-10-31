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

import { Directive, HostListener, Input, contentChild, effect, inject } from '@angular/core';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { IconDefinition, faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';

import { SortDirective } from './sort.directive';

@Directive({
  standalone: true,
  selector: '[jhiSortBy]',
})
export class SortByDirective {
  @Input() jhiSortBy!: string;

  iconComponent = contentChild(FaIconComponent);

  protected sortIcon = faSort;
  protected sortAscIcon = faSortUp;
  protected sortDescIcon = faSortDown;

  private readonly sort = inject(SortDirective, { host: true });

  constructor() {
    effect(() => {
      if (this.iconComponent()) {
        let icon: IconDefinition = this.sortIcon;
        const { predicate, order } = this.sort.sortState();
        if (predicate === this.jhiSortBy && order !== undefined) {
          icon = order === 'asc' ? this.sortAscIcon : this.sortDescIcon;
        }
        this.iconComponent()!.icon = icon.iconName;
        this.iconComponent()!.render();
      }
    });
  }

  @HostListener('click')
  onClick(): void {
    if (this.iconComponent()) {
      this.sort.sort(this.jhiSortBy);
    }
  }
}
