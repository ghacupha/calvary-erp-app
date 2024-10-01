import { IInstitution, NewInstitution } from './institution.model';

export const sampleWithRequiredData: IInstitution = {
  id: 8730,
  name: 'boohoo painfully income',
};

export const sampleWithPartialData: IInstitution = {
  id: 18247,
  name: 'bravely ew',
};

export const sampleWithFullData: IInstitution = {
  id: 16826,
  name: 'viciously',
};

export const sampleWithNewData: NewInstitution = {
  name: 'off',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
