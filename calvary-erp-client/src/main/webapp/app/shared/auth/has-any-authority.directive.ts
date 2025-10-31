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

import { Directive, TemplateRef, ViewContainerRef, computed, effect, inject, input } from '@angular/core';

import { AccountService } from 'app/core/auth/account.service';

/**
 * @whatItDoes Conditionally includes an HTML element if current user has any
 * of the authorities passed as the `expression`.
 *
 * @howToUse
 * ```
 *     <some-element *jhiHasAnyAuthority="'ROLE_ADMIN'">...</some-element>
 *
 *     <some-element *jhiHasAnyAuthority="['ROLE_ADMIN', 'ROLE_USER']">...</some-element>
 * ```
 */
@Directive({
  standalone: true,
  selector: '[jhiHasAnyAuthority]',
})
export default class HasAnyAuthorityDirective {
  public authorities = input<string | string[]>([], { alias: 'jhiHasAnyAuthority' });

  private readonly templateRef = inject(TemplateRef<any>);
  private readonly viewContainerRef = inject(ViewContainerRef);

  constructor() {
    const accountService = inject(AccountService);
    const currentAccount = accountService.trackCurrentAccount();
    const hasPermission = computed(() => currentAccount()?.authorities && accountService.hasAnyAuthority(this.authorities()));

    effect(
      () => {
        if (hasPermission()) {
          this.viewContainerRef.createEmbeddedView(this.templateRef);
        } else {
          this.viewContainerRef.clear();
        }
      },
      { allowSignalWrites: true },
    );
  }
}
