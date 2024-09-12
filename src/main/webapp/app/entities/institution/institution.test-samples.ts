import { IInstitution, NewInstitution } from './institution.model';

export const sampleWithRequiredData: IInstitution = {
  id: 90,
  institutionName: 'ew circa boo',
};

export const sampleWithPartialData: IInstitution = {
  id: 9933,
  institutionName: 'woodwind forenenst farmland',
};

export const sampleWithFullData: IInstitution = {
  id: 2594,
  institutionName: 'yearly sans',
};

export const sampleWithNewData: NewInstitution = {
  institutionName: 'clump',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
