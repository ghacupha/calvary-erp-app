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

import { IEntitySubscription, NewEntitySubscription } from './entity-subscription.model';

export const sampleWithRequiredData: IEntitySubscription = {
  id: 21327,
  subscriptionToken: '1efb7dd8-f7c7-49d2-b2a7-68a96b7c9048',
  startDate: dayjs('2024-10-01T03:24'),
  endDate: dayjs('2024-09-30T16:48'),
};

export const sampleWithPartialData: IEntitySubscription = {
  id: 2812,
  subscriptionToken: '24f564a5-bd68-4e89-9a8e-15a85779f57c',
  startDate: dayjs('2024-09-30T16:40'),
  endDate: dayjs('2024-09-30T15:06'),
};

export const sampleWithFullData: IEntitySubscription = {
  id: 24899,
  subscriptionToken: '7089e231-ac50-4adf-9281-cad679cb3657',
  startDate: dayjs('2024-10-01T07:16'),
  endDate: dayjs('2024-09-30T20:53'),
};

export const sampleWithNewData: NewEntitySubscription = {
  subscriptionToken: '1f8a0f52-c6df-4f54-ad8a-ce995d7abfd0',
  startDate: dayjs('2024-09-30T11:40'),
  endDate: dayjs('2024-10-01T01:39'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
