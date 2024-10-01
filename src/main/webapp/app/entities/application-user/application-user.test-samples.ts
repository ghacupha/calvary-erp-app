import dayjs from 'dayjs/esm';

import { IApplicationUser, NewApplicationUser } from './application-user.model';

export const sampleWithRequiredData: IApplicationUser = {
  id: 2592,
  username: 'although',
};

export const sampleWithPartialData: IApplicationUser = {
  id: 1808,
  username: 'sermon forenenst',
  firstName: 'Camron',
  lastName: 'Legros',
  email: 'Sheila39@hotmail.com',
  imageUrl: 'tunnel',
  activationKey: 'off',
};

export const sampleWithFullData: IApplicationUser = {
  id: 24786,
  username: 'date',
  firstName: 'Brenda',
  lastName: 'Homenick',
  email: 'Talia.Reinger@gmail.com',
  activated: true,
  langKey: 'upright',
  imageUrl: 'encourage unless so',
  activationKey: 'mortal spherical without',
  resetKey: 'rash whether',
  resetDate: dayjs('2024-09-30T05:07'),
};

export const sampleWithNewData: NewApplicationUser = {
  username: 'unwrap',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
