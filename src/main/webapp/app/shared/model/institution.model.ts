export interface IInstitution {
  id?: number;
  institutionName?: string;
  parentInstitution?: IInstitution | null;
}

export const defaultValue: Readonly<IInstitution> = {};
