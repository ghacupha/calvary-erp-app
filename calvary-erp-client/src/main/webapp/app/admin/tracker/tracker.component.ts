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

import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Subscription } from 'rxjs';

import { TrackerService } from 'app/core/tracker/tracker.service';
import { TrackerActivity } from 'app/core/tracker/tracker-activity.model';
import SharedModule from 'app/shared/shared.module';

@Component({
  standalone: true,
  selector: 'jhi-tracker',
  imports: [SharedModule],
  templateUrl: './tracker.component.html',
})
export default class TrackerComponent implements OnInit, OnDestroy {
  activities: TrackerActivity[] = [];
  subscription?: Subscription;

  private readonly trackerService = inject(TrackerService);

  showActivity(activity: TrackerActivity): void {
    let existingActivity = false;

    for (let index = 0; index < this.activities.length; index++) {
      if (this.activities[index].sessionId === activity.sessionId) {
        existingActivity = true;
        if (activity.page === 'logout') {
          this.activities.splice(index, 1);
        } else {
          this.activities[index] = activity;
        }
      }
    }

    if (!existingActivity && activity.page !== 'logout') {
      this.activities.push(activity);
    }
  }

  ngOnInit(): void {
    this.subscription = this.trackerService.subscribe({
      next: (activity: TrackerActivity) => {
        this.showActivity(activity);
      },
    });
    this.trackerService.sendActivity();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
