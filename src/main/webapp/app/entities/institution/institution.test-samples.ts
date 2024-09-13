import { IInstitution, NewInstitution } from './institution.model';

export const sampleWithRequiredData: IInstitution = {
  id: 1002,
  institutionName: 'glee yowza mid',
};

export const sampleWithPartialData: IInstitution = {
  id: 17760,
  institutionName: 'imperturbable',
};

export const sampleWithFullData: IInstitution = {
  id: 17631,
  institutionName: 'slot',
};

export const sampleWithNewData: NewInstitution = {
  institutionName: 'lest',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
