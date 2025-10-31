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
import { Location } from '@angular/common';
import { Event, NavigationEnd, Router } from '@angular/router';
import { Observer, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import SockJS from 'sockjs-client';
import { RxStomp } from '@stomp/rx-stomp';

import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';
import { AccountService } from '../auth/account.service';
import { Account } from '../auth/account.model';
import { TrackerActivity } from './tracker-activity.model';

const DESTINATION_TRACKER = '/topic/tracker';
const DESTINATION_ACTIVITY = '/topic/activity';

@Injectable({ providedIn: 'root' })
export class TrackerService {
  private rxStomp?: RxStomp;
  private routerSubscription: Subscription | null = null;

  private readonly router = inject(Router);
  private readonly accountService = inject(AccountService);
  private readonly authServerProvider = inject(AuthServerProvider);
  private readonly location = inject(Location);

  setup(): void {
    this.rxStomp = new RxStomp();
    this.rxStomp.configure({
      // eslint-disable-next-line no-console
      debug: (msg: string): void => console.log(new Date(), msg),
    });

    this.accountService.getAuthenticationState().subscribe({
      next: (account: Account | null) => {
        if (account) {
          this.connect();
        } else {
          this.disconnect();
        }
      },
    });

    this.rxStomp.connected$.subscribe(() => {
      this.sendActivity();

      this.routerSubscription = this.router.events
        .pipe(filter((event: Event) => event instanceof NavigationEnd))
        .subscribe(() => this.sendActivity());
    });
  }

  get stomp(): RxStomp {
    if (!this.rxStomp) {
      throw new Error('Stomp connection not initialized');
    }
    return this.rxStomp;
  }

  public subscribe(observer: Partial<Observer<TrackerActivity>>): Subscription {
    return (
      this.stomp
        .watch(DESTINATION_TRACKER)
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        .pipe(map(imessage => JSON.parse(imessage.body)))
        .subscribe(observer)
    );
  }

  sendActivity(): void {
    this.stomp.publish({
      destination: DESTINATION_ACTIVITY,
      body: JSON.stringify({ page: this.router.routerState.snapshot.url }),
    });
  }

  private connect(): void {
    this.updateCredentials();
    return this.stomp.activate();
  }

  private disconnect(): Promise<void> {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
      this.routerSubscription = null;
    }
    return this.stomp.deactivate();
  }

  private buildUrl(): string {
    // building absolute path so that websocket doesn't fail when deploying with a context path
    let url = '/websocket/tracker';
    url = this.location.prepareExternalUrl(url);
    const authToken = this.authServerProvider.getToken();
    if (authToken) {
      return `${url}?access_token=${authToken}`;
    }
    return url;
  }

  private updateCredentials(): void {
    this.stomp.configure({
      webSocketFactory: () => SockJS(this.buildUrl()),
    });
  }
}
