import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 12329,
  login: '6',
};

export const sampleWithPartialData: IUser = {
  id: 18234,
  login: 'tu--3@24-2\\@CQg8\\lK5Sat\\jaJ3S\\{MN\\TVVXyfm',
};

export const sampleWithFullData: IUser = {
  id: 27470,
  login: 'W@j',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
