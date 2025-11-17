import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IInstitution } from '../../registration/institution/institution.model';

@Component({
  standalone: true,
  selector: 'jhi-institution-option-view',
  template: ` # {{ item.id }} Name {{ item.name }} `,
  imports: [FormsModule],
})
export class InstitutionOptionViewComponent {
  @Input() item: IInstitution = { id: 0 };
}
