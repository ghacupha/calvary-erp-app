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

import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IInstitution } from 'app/entities/institution/institution.model';

export interface IApplicationUser {
  id: number;
  username?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  activated?: boolean | null;
  langKey?: string | null;
  imageUrl?: string | null;
  activationKey?: string | null;
  resetKey?: string | null;
  resetDate?: dayjs.Dayjs | null;
  systemUser?: Pick<IUser, 'id' | 'login'> | null;
  institution?: Pick<IInstitution, 'id' | 'name'> | null;
}

export type NewApplicationUser = Omit<IApplicationUser, 'id'> & { id: null };
