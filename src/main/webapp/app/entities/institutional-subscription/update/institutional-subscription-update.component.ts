import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IInstitution } from 'app/entities/institution/institution.model';
import { InstitutionService } from 'app/entities/institution/service/institution.service';
import { IInstitutionalSubscription } from '../institutional-subscription.model';
import { InstitutionalSubscriptionService } from '../service/institutional-subscription.service';
import { InstitutionalSubscriptionFormGroup, InstitutionalSubscriptionFormService } from './institutional-subscription-form.service';

@Component({
  standalone: true,
  selector: 'jhi-institutional-subscription-update',
  templateUrl: './institutional-subscription-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InstitutionalSubscriptionUpdateComponent implements OnInit {
  isSaving = false;
  institutionalSubscription: IInstitutionalSubscription | null = null;

  institutionsSharedCollection: IInstitution[] = [];

  protected institutionalSubscriptionService = inject(InstitutionalSubscriptionService);
  protected institutionalSubscriptionFormService = inject(InstitutionalSubscriptionFormService);
  protected institutionService = inject(InstitutionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InstitutionalSubscriptionFormGroup = this.institutionalSubscriptionFormService.createInstitutionalSubscriptionFormGroup();

  compareInstitution = (o1: IInstitution | null, o2: IInstitution | null): boolean => this.institutionService.compareInstitution(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ institutionalSubscription }) => {
      this.institutionalSubscription = institutionalSubscription;
      if (institutionalSubscription) {
        this.updateForm(institutionalSubscription);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const institutionalSubscription = this.institutionalSubscriptionFormService.getInstitutionalSubscription(this.editForm);
    if (institutionalSubscription.id !== null) {
      this.subscribeToSaveResponse(this.institutionalSubscriptionService.update(institutionalSubscription));
    } else {
      this.subscribeToSaveResponse(this.institutionalSubscriptionService.create(institutionalSubscription));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInstitutionalSubscription>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(institutionalSubscription: IInstitutionalSubscription): void {
    this.institutionalSubscription = institutionalSubscription;
    this.institutionalSubscriptionFormService.resetForm(this.editForm, institutionalSubscription);

    this.institutionsSharedCollection = this.institutionService.addInstitutionToCollectionIfMissing<IInstitution>(
      this.institutionsSharedCollection,
      institutionalSubscription.institution,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.institutionService
      .query()
      .pipe(map((res: HttpResponse<IInstitution[]>) => res.body ?? []))
      .pipe(
        map((institutions: IInstitution[]) =>
          this.institutionService.addInstitutionToCollectionIfMissing<IInstitution>(
            institutions,
            this.institutionalSubscription?.institution,
          ),
        ),
      )
      .subscribe((institutions: IInstitution[]) => (this.institutionsSharedCollection = institutions));
  }
}
