import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 6592,
  login: 'O',
};

export const sampleWithPartialData: IUser = {
  id: 3906,
  login: '_kd',
};

export const sampleWithFullData: IUser = {
  id: 32353,
  login: '6@Mztb\\nD\\<QLp\\aIn4c\\tGW',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
