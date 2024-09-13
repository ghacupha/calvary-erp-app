import dayjs from 'dayjs/esm';
import { IInstitution } from 'app/entities/institution/institution.model';

export interface IInstitutionalSubscription {
  id: number;
  startDate?: dayjs.Dayjs | null;
  expiryDate?: dayjs.Dayjs | null;
  memberLimit?: number | null;
  institution?: Pick<IInstitution, 'id' | 'institutionName'> | null;
}

export type NewInstitutionalSubscription = Omit<IInstitutionalSubscription, 'id'> & { id: null };
