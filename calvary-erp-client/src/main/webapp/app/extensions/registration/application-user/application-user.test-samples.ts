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

import { IApplicationUser, NewApplicationUser } from './application-user.model';

export const sampleWithRequiredData: IApplicationUser = {
  id: 19017,
  username: 'fooey little offend',
};

export const sampleWithPartialData: IApplicationUser = {
  id: 25246,
  username: 'till',
  firstName: 'Doug',
  lastName: 'Sawayn',
  email: 'Erick.Weber-Auer@yahoo.com',
  langKey: 'once inject',
  resetKey: 'opposite ugh admonish',
  resetDate: dayjs('2024-09-30T09:53'),
};

export const sampleWithFullData: IApplicationUser = {
  id: 7417,
  username: 'devise cautiously',
  firstName: 'Billie',
  lastName: 'Dibbert',
  email: 'Cornell_Predovic95@hotmail.com',
  activated: true,
  langKey: 'helpfully daily',
  imageUrl: 'grass however outside',
  activationKey: 'ugh elastic',
  resetKey: 'irritably',
  resetDate: dayjs('2024-09-30T11:13'),
};

export const sampleWithNewData: NewApplicationUser = {
  username: 'neglect lively',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
