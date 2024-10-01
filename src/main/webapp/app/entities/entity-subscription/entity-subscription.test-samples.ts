import dayjs from 'dayjs/esm';

import { IEntitySubscription, NewEntitySubscription } from './entity-subscription.model';

export const sampleWithRequiredData: IEntitySubscription = {
  id: 1117,
  subscriptionToken: '20fc5f47-3e09-4440-9564-1b453f82e689',
  startDate: dayjs('2024-09-30T12:40'),
  endDate: dayjs('2024-09-30T18:09'),
};

export const sampleWithPartialData: IEntitySubscription = {
  id: 16912,
  subscriptionToken: 'cfc259e4-b8ea-4268-a20b-dad7afd615d2',
  startDate: dayjs('2024-10-01T10:23'),
  endDate: dayjs('2024-09-30T10:43'),
};

export const sampleWithFullData: IEntitySubscription = {
  id: 15214,
  subscriptionToken: 'e345637a-9997-4660-bd75-1666028cb1c2',
  startDate: dayjs('2024-10-01T10:09'),
  endDate: dayjs('2024-09-30T20:15'),
};

export const sampleWithNewData: NewEntitySubscription = {
  subscriptionToken: '062235d4-ffb7-4d60-b073-8d3297b67e5c',
  startDate: dayjs('2024-10-01T03:18'),
  endDate: dayjs('2024-09-30T18:15'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
