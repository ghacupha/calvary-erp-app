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

import { TestBed, inject } from '@angular/core/testing';

import { EventManager, EventWithContent } from './event-manager.service';

describe('Event Manager tests', () => {
  describe('EventWithContent', () => {
    it('should create correctly EventWithContent', () => {
      // WHEN
      const eventWithContent = new EventWithContent('name', 'content');

      // THEN
      expect(eventWithContent).toEqual({ name: 'name', content: 'content' });
    });
  });

  describe('EventManager', () => {
    let receivedEvent: EventWithContent<unknown> | string | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [EventManager],
      });
      receivedEvent = null;
    });

    it('should not fail when nosubscriber and broadcasting', inject([EventManager], (eventManager: EventManager) => {
      expect(eventManager.observer).toBeUndefined();
      eventManager.broadcast({ name: 'modifier', content: 'modified something' });
    }));

    it('should create an observable and callback when broadcasted EventWithContent', inject(
      [EventManager],
      (eventManager: EventManager) => {
        // GIVEN
        eventManager.subscribe('modifier', (event: EventWithContent<unknown> | string) => (receivedEvent = event));

        // WHEN
        eventManager.broadcast({ name: 'unrelatedModifier', content: 'unrelated modification' });
        // THEN
        expect(receivedEvent).toBeNull();

        // WHEN
        eventManager.broadcast({ name: 'modifier', content: 'modified something' });
        // THEN
        expect(receivedEvent).toEqual({ name: 'modifier', content: 'modified something' });
      },
    ));

    it('should create an observable and callback when broadcasted string', inject([EventManager], (eventManager: EventManager) => {
      // GIVEN
      eventManager.subscribe('modifier', (event: EventWithContent<unknown> | string) => (receivedEvent = event));

      // WHEN
      eventManager.broadcast('unrelatedModifier');
      // THEN
      expect(receivedEvent).toBeNull();

      // WHEN
      eventManager.broadcast('modifier');
      // THEN
      expect(receivedEvent).toEqual('modifier');
    }));

    it('should subscribe to multiple events', inject([EventManager], (eventManager: EventManager) => {
      // GIVEN
      eventManager.subscribe(['modifier', 'modifier2'], (event: EventWithContent<unknown> | string) => (receivedEvent = event));

      // WHEN
      eventManager.broadcast('unrelatedModifier');
      // THEN
      expect(receivedEvent).toBeNull();

      // WHEN
      eventManager.broadcast({ name: 'modifier', content: 'modified something' });
      // THEN
      expect(receivedEvent).toEqual({ name: 'modifier', content: 'modified something' });

      // WHEN
      eventManager.broadcast('modifier2');
      // THEN
      expect(receivedEvent).toEqual('modifier2');
    }));
  });
});
