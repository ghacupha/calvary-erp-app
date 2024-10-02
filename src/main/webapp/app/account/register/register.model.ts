export class Registration {
  constructor(
    public login: string,
    public email: string,
    public password: string,
    public langKey: string,
    public institutionId?: string,
    public firstName?: string,
    public lastName?: string,
  ) {}
}
