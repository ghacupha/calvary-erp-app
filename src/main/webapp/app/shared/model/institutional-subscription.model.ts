import dayjs from 'dayjs';
import { IInstitution } from 'app/shared/model/institution.model';

export interface IInstitutionalSubscription {
  id?: number;
  startDate?: dayjs.Dayjs;
  expiryDate?: dayjs.Dayjs;
  memberLimit?: number;
  institution?: IInstitution;
}

export const defaultValue: Readonly<IInstitutionalSubscription> = {};
