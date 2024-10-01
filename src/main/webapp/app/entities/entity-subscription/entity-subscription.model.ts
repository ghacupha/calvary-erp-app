import dayjs from 'dayjs/esm';
import { IInstitution } from 'app/entities/institution/institution.model';

export interface IEntitySubscription {
  id: number;
  subscriptionToken?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  institution?: Pick<IInstitution, 'id' | 'name'> | null;
}

export type NewEntitySubscription = Omit<IEntitySubscription, 'id'> & { id: null };
