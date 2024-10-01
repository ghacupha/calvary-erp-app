import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IInstitution } from 'app/entities/institution/institution.model';
import { InstitutionService } from 'app/entities/institution/service/institution.service';
import { IEntitySubscription } from '../entity-subscription.model';
import { EntitySubscriptionService } from '../service/entity-subscription.service';
import { EntitySubscriptionFormGroup, EntitySubscriptionFormService } from './entity-subscription-form.service';

@Component({
  standalone: true,
  selector: 'jhi-entity-subscription-update',
  templateUrl: './entity-subscription-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EntitySubscriptionUpdateComponent implements OnInit {
  isSaving = false;
  entitySubscription: IEntitySubscription | null = null;

  institutionsSharedCollection: IInstitution[] = [];

  protected entitySubscriptionService = inject(EntitySubscriptionService);
  protected entitySubscriptionFormService = inject(EntitySubscriptionFormService);
  protected institutionService = inject(InstitutionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EntitySubscriptionFormGroup = this.entitySubscriptionFormService.createEntitySubscriptionFormGroup();

  compareInstitution = (o1: IInstitution | null, o2: IInstitution | null): boolean => this.institutionService.compareInstitution(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ entitySubscription }) => {
      this.entitySubscription = entitySubscription;
      if (entitySubscription) {
        this.updateForm(entitySubscription);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const entitySubscription = this.entitySubscriptionFormService.getEntitySubscription(this.editForm);
    if (entitySubscription.id !== null) {
      this.subscribeToSaveResponse(this.entitySubscriptionService.update(entitySubscription));
    } else {
      this.subscribeToSaveResponse(this.entitySubscriptionService.create(entitySubscription));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEntitySubscription>>): void {
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

  protected updateForm(entitySubscription: IEntitySubscription): void {
    this.entitySubscription = entitySubscription;
    this.entitySubscriptionFormService.resetForm(this.editForm, entitySubscription);

    this.institutionsSharedCollection = this.institutionService.addInstitutionToCollectionIfMissing<IInstitution>(
      this.institutionsSharedCollection,
      entitySubscription.institution,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.institutionService
      .query()
      .pipe(map((res: HttpResponse<IInstitution[]>) => res.body ?? []))
      .pipe(
        map((institutions: IInstitution[]) =>
          this.institutionService.addInstitutionToCollectionIfMissing<IInstitution>(institutions, this.entitySubscription?.institution),
        ),
      )
      .subscribe((institutions: IInstitution[]) => (this.institutionsSharedCollection = institutions));
  }
}
