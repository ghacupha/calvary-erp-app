import { IInstitution, NewInstitution } from './institution.model';

export const sampleWithRequiredData: IInstitution = {
  id: 6132,
  name: 'before amidst',
};

export const sampleWithPartialData: IInstitution = {
  id: 22391,
  name: 'hence',
};

export const sampleWithFullData: IInstitution = {
  id: 14895,
  name: 'carefully ew',
};

export const sampleWithNewData: NewInstitution = {
  name: 'clinch',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
