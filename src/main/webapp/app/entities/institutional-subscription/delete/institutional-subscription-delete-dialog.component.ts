import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IInstitutionalSubscription } from '../institutional-subscription.model';
import { InstitutionalSubscriptionService } from '../service/institutional-subscription.service';

@Component({
  standalone: true,
  templateUrl: './institutional-subscription-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class InstitutionalSubscriptionDeleteDialogComponent {
  institutionalSubscription?: IInstitutionalSubscription;

  protected institutionalSubscriptionService = inject(InstitutionalSubscriptionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.institutionalSubscriptionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
