export interface IInstitution {
  id: number;
  institutionName?: string | null;
  parentInstitution?: Pick<IInstitution, 'id' | 'institutionName'> | null;
}

export type NewInstitution = Omit<IInstitution, 'id'> & { id: null };
