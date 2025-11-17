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

// erp-extensions.module.ts
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { EntitySubscriptionService } from 'app/entities/entity-subscription/service/entity-subscription.service';
import { InstitutionService } from 'app/entities/institution/service/institution.service';
import { ApplicationUserDeleteDialogComponent } from '../entities/application-user/delete/application-user-delete-dialog.component';
import { ApplicationUserComponent } from '../entities/application-user/list/application-user.component';
import { ERPApplicationUserDeleteDialogComponent } from './registration/application-user/delete/application-user-delete-dialog.component';
import { ERPApplicationUserComponent } from './registration/application-user/list/application-user.component';
import { ERPApplicationUserService } from './registration/application-user/service/application-user.service';
import { ERPEntitySubscriptionService } from './registration/entity-subscription/service/entity-subscription.service';
import { ERPInstitutionService } from './registration/institution/service/institution.service';

import { Provider } from '@angular/core';
import { ApplicationUserUpdateComponent } from 'app/entities/application-user/update/application-user-update.component';
import { ERPApplicationUserUpdateComponent } from './registration/application-user/update/application-user-update.component';
import { Routes, ROUTES } from '@angular/router';
import applicationUserOverrideRoutes from './registration/application-user/application-user-override.routes';

/**
 * This configuration is to override generated component routes
 */
const extensionRoutes: Routes = [
  // Custom component routes to override generated routes
  ...applicationUserOverrideRoutes,
];

/**
 * Extensions for components that have been extended in the extensions
 * folder
 */
export const erpExtensionProviders: Provider[] = [
  // Route overrides
  {
    provide: ROUTES,
    useValue: extensionRoutes,
    multi: true,
  },
  // Import the tokens/services you need to override
  { provide: ApplicationUserService, useClass: ERPApplicationUserService },
  { provide: ApplicationUserDeleteDialogComponent, useClass: ERPApplicationUserDeleteDialogComponent },
  { provide: ApplicationUserComponent, useClass: ERPApplicationUserComponent },
  { provide: ApplicationUserUpdateComponent, useClass: ERPApplicationUserUpdateComponent },
  { provide: EntitySubscriptionService, useClass: ERPEntitySubscriptionService },
  { provide: InstitutionService, useClass: ERPInstitutionService },
];
