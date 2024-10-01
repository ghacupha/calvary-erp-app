import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IInstitution } from 'app/entities/institution/institution.model';

export interface IApplicationUser {
  id: number;
  username?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  activated?: boolean | null;
  langKey?: string | null;
  imageUrl?: string | null;
  activationKey?: string | null;
  resetKey?: string | null;
  resetDate?: dayjs.Dayjs | null;
  systemUser?: Pick<IUser, 'id' | 'login'> | null;
  institution?: Pick<IInstitution, 'id' | 'name'> | null;
}

export type NewApplicationUser = Omit<IApplicationUser, 'id'> & { id: null };
