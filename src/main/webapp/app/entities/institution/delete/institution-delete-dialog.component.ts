import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IInstitution } from '../institution.model';
import { InstitutionService } from '../service/institution.service';

@Component({
  standalone: true,
  templateUrl: './institution-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class InstitutionDeleteDialogComponent {
  institution?: IInstitution;

  protected institutionService = inject(InstitutionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.institutionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
