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
  id: 4500,
  subscriptionToken: 'ffc0f710-9e92-4e25-9154-8ab17fc6a605',
  startDate: dayjs('2024-09-30T21:32'),
  endDate: dayjs('2024-09-30T14:00'),
};

export const sampleWithPartialData: IEntitySubscription = {
  id: 5984,
  subscriptionToken: '5ba0712e-01a5-46df-8846-838c00665573',
  startDate: dayjs('2024-10-01T03:07'),
  endDate: dayjs('2024-09-30T14:12'),
};

export const sampleWithFullData: IEntitySubscription = {
  id: 18989,
  subscriptionToken: '6c161407-0400-469c-9425-110d0f7b3a6e',
  startDate: dayjs('2024-09-30T16:42'),
  endDate: dayjs('2024-10-01T09:41'),
};

export const sampleWithNewData: NewEntitySubscription = {
  subscriptionToken: '1a58072c-81bf-46ba-b56e-13b9a288aae4',
  startDate: dayjs('2024-09-30T22:09'),
  endDate: dayjs('2024-10-01T08:18'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
