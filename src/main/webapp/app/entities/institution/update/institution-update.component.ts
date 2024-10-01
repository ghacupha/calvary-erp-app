import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IInstitution } from '../institution.model';
import { InstitutionService } from '../service/institution.service';
import { InstitutionFormGroup, InstitutionFormService } from './institution-form.service';

@Component({
  standalone: true,
  selector: 'jhi-institution-update',
  templateUrl: './institution-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InstitutionUpdateComponent implements OnInit {
  isSaving = false;
  institution: IInstitution | null = null;

  protected institutionService = inject(InstitutionService);
  protected institutionFormService = inject(InstitutionFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InstitutionFormGroup = this.institutionFormService.createInstitutionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ institution }) => {
      this.institution = institution;
      if (institution) {
        this.updateForm(institution);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const institution = this.institutionFormService.getInstitution(this.editForm);
    if (institution.id !== null) {
      this.subscribeToSaveResponse(this.institutionService.update(institution));
    } else {
      this.subscribeToSaveResponse(this.institutionService.create(institution));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInstitution>>): void {
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

  protected updateForm(institution: IInstitution): void {
    this.institution = institution;
    this.institutionFormService.resetForm(this.editForm, institution);
  }
}
