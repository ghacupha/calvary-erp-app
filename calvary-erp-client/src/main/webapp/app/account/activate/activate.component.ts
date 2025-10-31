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

import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { mergeMap } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { ActivateService } from './activate.service';

@Component({
  standalone: true,
  selector: 'jhi-activate',
  imports: [SharedModule, RouterModule],
  templateUrl: './activate.component.html',
})
export default class ActivateComponent implements OnInit {
  error = signal(false);
  success = signal(false);

  private readonly activateService = inject(ActivateService);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParams.pipe(mergeMap(params => this.activateService.get(params.key))).subscribe({
      next: () => this.success.set(true),
      error: () => this.error.set(true),
    });
  }
}
