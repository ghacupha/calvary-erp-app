import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '6754adb9-3118-4910-9ed2-ad979a3a1f28',
};

export const sampleWithPartialData: IAuthority = {
  name: '6c912bdf-60fb-4f97-881d-4993878e8b87',
};

export const sampleWithFullData: IAuthority = {
  name: 'cb3612b2-98af-40ac-be0c-0a94ba20cb2b',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
