import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IApplicationUser } from '../application-user.model';

@Component({
  standalone: true,
  selector: 'jhi-application-user-detail',
  templateUrl: './application-user-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ApplicationUserDetailComponent {
  applicationUser = input<IApplicationUser | null>(null);

  previousState(): void {
    window.history.back();
  }
}
