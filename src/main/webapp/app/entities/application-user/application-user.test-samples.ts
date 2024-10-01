import dayjs from 'dayjs/esm';

import { IApplicationUser, NewApplicationUser } from './application-user.model';

export const sampleWithRequiredData: IApplicationUser = {
  id: 8770,
  username: 'wide stealthily',
};

export const sampleWithPartialData: IApplicationUser = {
  id: 2779,
  username: 'oh unto barring',
  lastName: 'Mann',
  activated: true,
  langKey: 'between',
  resetKey: 'uh-huh dimly',
};

export const sampleWithFullData: IApplicationUser = {
  id: 10749,
  username: 'meanwhile converse',
  firstName: 'Krystal',
  lastName: 'Ferry',
  email: 'Saige_Dooley@hotmail.com',
  activated: true,
  langKey: 'until',
  imageUrl: 'sick',
  activationKey: 'gallop gentleman',
  resetKey: 'surprisingly exactly',
  resetDate: dayjs('2024-09-30T08:39'),
};

export const sampleWithNewData: NewApplicationUser = {
  username: 'now',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
