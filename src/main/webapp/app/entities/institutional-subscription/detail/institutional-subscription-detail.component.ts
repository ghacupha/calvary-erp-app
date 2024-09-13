import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IInstitutionalSubscription } from '../institutional-subscription.model';

@Component({
  standalone: true,
  selector: 'jhi-institutional-subscription-detail',
  templateUrl: './institutional-subscription-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class InstitutionalSubscriptionDetailComponent {
  institutionalSubscription = input<IInstitutionalSubscription | null>(null);

  previousState(): void {
    window.history.back();
  }
}
