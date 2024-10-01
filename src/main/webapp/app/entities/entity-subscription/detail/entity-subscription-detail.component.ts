import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IEntitySubscription } from '../entity-subscription.model';

@Component({
  standalone: true,
  selector: 'jhi-entity-subscription-detail',
  templateUrl: './entity-subscription-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class EntitySubscriptionDetailComponent {
  entitySubscription = input<IEntitySubscription | null>(null);

  previousState(): void {
    window.history.back();
  }
}
