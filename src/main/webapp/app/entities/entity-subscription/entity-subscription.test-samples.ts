import dayjs from 'dayjs/esm';

import { IEntitySubscription, NewEntitySubscription } from './entity-subscription.model';

export const sampleWithRequiredData: IEntitySubscription = {
  id: 15038,
  subscriptionToken: 'bc31479d-42b6-4f1d-8193-9c405b6f38a3',
  startDate: dayjs('2024-09-30T16:12'),
  endDate: dayjs('2024-09-30T17:22'),
};

export const sampleWithPartialData: IEntitySubscription = {
  id: 3721,
  subscriptionToken: '1e2ca349-ee1c-42b8-9f4f-a62b51f7761f',
  startDate: dayjs('2024-10-01T01:17'),
  endDate: dayjs('2024-10-01T01:48'),
};

export const sampleWithFullData: IEntitySubscription = {
  id: 7823,
  subscriptionToken: '5b0f51f9-cb36-4749-86e2-7e30ca2c4c47',
  startDate: dayjs('2024-10-01T02:28'),
  endDate: dayjs('2024-09-30T23:48'),
};

export const sampleWithNewData: NewEntitySubscription = {
  subscriptionToken: '2da92803-3468-451c-a55a-e1152637e640',
  startDate: dayjs('2024-09-30T15:58'),
  endDate: dayjs('2024-10-01T02:14'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
