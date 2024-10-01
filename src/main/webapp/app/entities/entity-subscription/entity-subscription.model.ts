import dayjs from 'dayjs/esm';

export interface IEntitySubscription {
  id: number;
  subscriptionToken?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
}

export type NewEntitySubscription = Omit<IEntitySubscription, 'id'> & { id: null };
