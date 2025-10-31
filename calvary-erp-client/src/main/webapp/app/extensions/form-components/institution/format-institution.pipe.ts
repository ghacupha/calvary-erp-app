import { Pipe, PipeTransform } from '@angular/core';
import { IInstitution } from '../../registration/institution/institution.model';

@Pipe({
  name: 'formatInstitution',
  standalone: true,
})
export class FormatInstitutionPipe implements PipeTransform {
  transform(value: IInstitution): string {
    return `Id: ${value.id} | Name: ${value.name}`;
  }
}
