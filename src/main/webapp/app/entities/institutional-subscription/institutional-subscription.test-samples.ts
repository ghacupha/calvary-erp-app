import dayjs from 'dayjs/esm';

import { IInstitutionalSubscription, NewInstitutionalSubscription } from './institutional-subscription.model';

export const sampleWithRequiredData: IInstitutionalSubscription = {
  id: 23269,
  startDate: dayjs('2024-09-13'),
  expiryDate: dayjs('2024-09-13'),
  memberLimit: 18385,
};

export const sampleWithPartialData: IInstitutionalSubscription = {
  id: 10512,
  startDate: dayjs('2024-09-13'),
  expiryDate: dayjs('2024-09-13'),
  memberLimit: 24328,
};

export const sampleWithFullData: IInstitutionalSubscription = {
  id: 13484,
  startDate: dayjs('2024-09-13'),
  expiryDate: dayjs('2024-09-13'),
  memberLimit: 792,
};

export const sampleWithNewData: NewInstitutionalSubscription = {
  startDate: dayjs('2024-09-13'),
  expiryDate: dayjs('2024-09-13'),
  memberLimit: 12703,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
