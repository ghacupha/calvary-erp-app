import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '4d0f562d-28a3-45b8-80e8-5582256e1b2f',
};

export const sampleWithPartialData: IAuthority = {
  name: '2bdea2d7-e3da-46ee-811a-c66596db0573',
};

export const sampleWithFullData: IAuthority = {
  name: '6c0e1180-ad5c-4804-9132-40727f5eb146',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
