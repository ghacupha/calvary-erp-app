export interface IInstitution {
  id: number;
  name?: string | null;
}

export type NewInstitution = Omit<IInstitution, 'id'> & { id: null };
