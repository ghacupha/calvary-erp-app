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

import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import SharedModule from 'app/shared/shared.module';
import { ProcessMetrics } from 'app/admin/metrics/metrics.model';

@Component({
  standalone: true,
  selector: 'jhi-metrics-system',
  templateUrl: './metrics-system.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule],
})
export class MetricsSystemComponent {
  /**
   * object containing thread related metrics
   */
  systemMetrics = input<ProcessMetrics>();

  /**
   * boolean field saying if the metrics are in the process of being updated
   */
  updating = input<boolean>();

  convertMillisecondsToDuration(ms: number): string {
    const times = {
      year: 31557600000,
      month: 2629746000,
      day: 86400000,
      hour: 3600000,
      minute: 60000,
      second: 1000,
    };
    let timeString = '';
    for (const [key, value] of Object.entries(times)) {
      if (Math.floor(ms / value) > 0) {
        let plural = '';
        if (Math.floor(ms / value) > 1) {
          plural = 's';
        }
        timeString += `${Math.floor(ms / value).toString()} ${key.toString()}${plural} `;
        ms = ms - value * Math.floor(ms / value);
      }
    }
    return timeString;
  }
}
