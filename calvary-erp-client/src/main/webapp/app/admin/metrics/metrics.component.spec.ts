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

import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';

import MetricsComponent from './metrics.component';
import { MetricsService } from './metrics.service';
import { Metrics, Thread, ThreadDump } from './metrics.model';

describe('MetricsComponent', () => {
  let comp: MetricsComponent;
  let fixture: ComponentFixture<MetricsComponent>;
  let service: MetricsService;
  let changeDetector: ChangeDetectorRef;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [MetricsComponent],
      providers: [provideHttpClient()],
    })
      .overrideTemplate(MetricsComponent, '')
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MetricsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MetricsService);
    changeDetector = fixture.debugElement.injector.get(ChangeDetectorRef);
  });

  describe('refresh', () => {
    it('should call refresh on init', () => {
      // GIVEN
      const metrics = {
        garbageCollector: {
          'PS Scavenge': {
            collectionCount: 0,
            collectionTime: 0,
          },
          'PS MarkSweep': {
            collectionCount: 0,
            collectionTime: 0,
          },
        },
      } as unknown as Metrics;
      const threadDump = { threads: [{ threadName: 'thread 1' } as Thread] } as ThreadDump;

      jest.spyOn(service, 'getMetrics').mockReturnValue(of(metrics));
      jest.spyOn(service, 'threadDump').mockReturnValue(of(threadDump));
      jest.spyOn(changeDetector.constructor.prototype, 'markForCheck');

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.getMetrics).toHaveBeenCalled();
      expect(comp.metrics()).toEqual(metrics);
      expect(comp.threads()).toEqual(threadDump.threads);
      expect(comp.updatingMetrics()).toBeFalsy();
      expect(changeDetector.constructor.prototype.markForCheck).toHaveBeenCalled();
    });
  });

  describe('metricsKeyExistsAndObjectNotEmpty', () => {
    it('should check that metrics key exists and is not empty', () => {
      // GIVEN
      comp.metrics.set({
        garbageCollector: {
          'PS Scavenge': {
            collectionCount: 0,
            collectionTime: 0,
          },
          'PS MarkSweep': {
            collectionCount: 0,
            collectionTime: 0,
          },
        },
      } as unknown as Metrics);

      // WHEN
      const garbageCollectorKeyExistsAndNotEmpty = comp.metricsKeyExistsAndObjectNotEmpty('garbageCollector');

      // THEN
      expect(garbageCollectorKeyExistsAndNotEmpty).toBeTruthy();
    });

    it('should check that metrics key is empty', () => {
      // GIVEN
      comp.metrics.set({
        garbageCollector: {},
      } as Metrics);

      // WHEN
      const garbageCollectorKeyEmpty = comp.metricsKeyExistsAndObjectNotEmpty('garbageCollector');

      // THEN
      expect(garbageCollectorKeyEmpty).toBeFalsy();
    });
  });
});
