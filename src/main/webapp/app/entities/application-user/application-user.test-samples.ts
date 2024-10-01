import dayjs from 'dayjs/esm';

import { IApplicationUser, NewApplicationUser } from './application-user.model';

export const sampleWithRequiredData: IApplicationUser = {
  id: 16892,
  username: 'mister mid anenst',
};

export const sampleWithPartialData: IApplicationUser = {
  id: 1504,
  username: 'slim',
  lastName: 'Jenkins',
  activated: false,
  resetDate: dayjs('2024-09-30T06:25'),
};

export const sampleWithFullData: IApplicationUser = {
  id: 5454,
  username: 'to beside fooey',
  firstName: 'Mustafa',
  lastName: 'Ferry',
  email: 'Leonor.Bashirian53@yahoo.com',
  activated: true,
  langKey: 'so recession',
  imageUrl: 'after with',
  activationKey: 'gloom',
  resetKey: 'sneaky considering jubilantly',
  resetDate: dayjs('2024-09-30T03:05'),
};

export const sampleWithNewData: NewApplicationUser = {
  username: 'near rare cabin',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
