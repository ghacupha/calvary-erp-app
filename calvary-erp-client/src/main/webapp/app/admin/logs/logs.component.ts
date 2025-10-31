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

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule } from '@angular/forms';
import { SortByDirective, SortDirective, SortService, sortStateSignal } from 'app/shared/sort';
import { Level, Log, LoggersResponse } from './log.model';
import { LogsService } from './logs.service';

@Component({
  standalone: true,
  selector: 'jhi-logs',
  templateUrl: './logs.component.html',
  imports: [SharedModule, FormsModule, SortDirective, SortByDirective],
})
export default class LogsComponent implements OnInit {
  loggers = signal<Log[] | undefined>(undefined);
  isLoading = signal(false);
  filter = signal('');
  sortState = sortStateSignal({ predicate: 'name', order: 'asc' });
  filteredAndOrderedLoggers = computed<Log[] | undefined>(() => {
    let data = this.loggers() ?? [];
    const filter = this.filter();
    if (filter) {
      data = data.filter(logger => logger.name.toLowerCase().includes(filter.toLowerCase()));
    }

    const { order, predicate } = this.sortState();
    if (order && predicate) {
      data = data.sort(this.sortService.startSort({ order, predicate }, { predicate: 'name', order: 'asc' }));
    }
    return data;
  });

  private readonly logsService = inject(LogsService);
  private readonly sortService = inject(SortService);

  ngOnInit(): void {
    this.findAndExtractLoggers();
  }

  changeLevel(name: string, level: Level): void {
    this.logsService.changeLevel(name, level).subscribe(() => this.findAndExtractLoggers());
  }

  private findAndExtractLoggers(): void {
    this.isLoading.set(true);
    this.logsService
      .findAll()
      .pipe(
        finalize(() => {
          this.isLoading.set(false);
        }),
      )
      .subscribe({
        next: (response: LoggersResponse) =>
          this.loggers.set(Object.entries(response.loggers).map(([key, logger]) => new Log(key, logger.effectiveLevel))),
        error: () => this.loggers.set([]),
      });
  }
}
