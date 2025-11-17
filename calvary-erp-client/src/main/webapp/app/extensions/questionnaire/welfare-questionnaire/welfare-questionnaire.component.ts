///
/// Erp System - Mark I No 2 (Beniah Series) Client 0.0.1-SNAPSHOT
/// Copyright © 2022 - 2025 Edwin Njeru (mailnjeru@gmail.com)
///
/// This program is free software: you can redistribute it and/or modify
/// it under the terms of the GNU General Public License as published by
/// the Free Software Foundation, either version 3 of the License, or
/// (at your option) any later version.
///
/// This program is distributed in the hope that it will be useful,
/// but WITHOUT ANY WARRANTY; without even the implied warranty of
/// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/// GNU General Public License for more details.
///
/// You should have received a copy of the GNU General Public License
/// along with this program. If not, see <http://www.gnu.org/licenses/>.
///

import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { QuestionnaireFormBuilderService } from '../questionnaire-form-builder.service';
import { QuestionnaireDefinition, QuestionnaireQuestion, QuestionnaireSection } from '../questionnaire.model';
import { QuestionnaireService } from '../questionnaire.service';

@Component({
  standalone: true,
  selector: 'jhi-welfare-questionnaire',
  templateUrl: './welfare-questionnaire.component.html',
  imports: [CommonModule, ReactiveFormsModule],
})
export class WelfareQuestionnaireComponent implements OnInit {
  readonly questionnaire = signal<QuestionnaireDefinition | null>(null);
  readonly loading = signal(true);
  readonly submitting = signal(false);
  readonly submissionSucceeded = signal(false);
  readonly submissionFailed = signal(false);

  readonly form = signal<FormGroup | null>(null);

  readonly submitLabel = computed(() => this.questionnaire()?.submitLabel ?? 'Submit');

  private readonly questionnaireService = inject(QuestionnaireService);
  private readonly formBuilderService = inject(QuestionnaireFormBuilderService);

  ngOnInit(): void {
    this.loadQuestionnaire();
  }

  addSection(section: QuestionnaireSection): void {
    const form = this.form();

    if (!form) {
      return;
    }

    this.formBuilderService.addSectionItem(form, section);
  }

  removeSection(section: QuestionnaireSection, index: number): void {
    const form = this.form();

    if (!form) {
      return;
    }

    this.formBuilderService.removeSectionItem(form, section.id, index, section.minimumEntries ?? 1);
  }

  sectionArray(sectionId: string): FormArray<FormGroup> | null {
    const form = this.form();

    if (!form) {
      return null;
    }

    const control = form.get(sectionId);

    return control instanceof FormArray ? (control as FormArray<FormGroup>) : null;
  }

  sectionGroup(sectionId: string): FormGroup | null {
    const form = this.form();

    if (!form) {
      return null;
    }

    const control = form.get(sectionId);

    return control instanceof FormGroup ? control : null;
  }

  questionControl(group: FormGroup, question: QuestionnaireQuestion): FormControl {
    const control = group.get(question.id);

    if (!(control instanceof FormControl)) {
      throw new Error(`Control '${question.id}' is missing in section.`);
    }

    return control;
  }

  submit(): void {
    const form = this.form();
    const questionnaire = this.questionnaire();

    if (!form || !questionnaire || form.invalid) {
      form?.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.submissionFailed.set(false);
    this.submissionSucceeded.set(false);

    const payload = {
      questionnaireId: questionnaire.id,
      responses: form.getRawValue(),
    };

    this.questionnaireService.submitQuestionnaire(questionnaire.id, payload).subscribe({
      next: () => {
        this.submitting.set(false);
        this.submissionSucceeded.set(true);
      },
      error: () => {
        this.submitting.set(false);
        this.submissionFailed.set(true);
      },
    });
  }

  showAddButton(section: QuestionnaireSection): boolean {
    const array = this.sectionArray(section.id);

    if (!array) {
      return false;
    }

    if (section.maximumEntries === undefined) {
      return true;
    }

    return array.length < section.maximumEntries;
  }

  canRemove(section: QuestionnaireSection, index: number): boolean {
    const array = this.sectionArray(section.id);

    if (!array) {
      return false;
    }

    const minimum = section.minimumEntries ?? 1;

    return array.length > minimum && index >= 0;
  }

  trackByIndex(index: number): number {
    return index;
  }

  errorMessage(control: FormControl, question: QuestionnaireQuestion): string | null {
    if (!(control.touched || control.dirty) || !control.errors) {
      return null;
    }

    if (control.hasError('required')) {
      return 'This field is required.';
    }

    if (control.hasError('pattern')) {
      return question.validation?.customMessage ?? 'Invalid format.';
    }

    if (control.hasError('minlength')) {
      return `Minimum length is ${question.validation?.minLength}.`;
    }

    if (control.hasError('maxlength')) {
      return `Maximum length is ${question.validation?.maxLength}.`;
    }

    if (control.hasError('min')) {
      return `Minimum value is ${question.validation?.min}.`;
    }

    if (control.hasError('max')) {
      return `Maximum value is ${question.validation?.max}.`;
    }

    return 'Invalid value.';
  }

  private loadQuestionnaire(): void {
    this.loading.set(true);
    this.questionnaireService.fetchQuestionnaire('welfare-membership').subscribe({
      next: definition => {
        this.questionnaire.set(definition);
        this.form.set(this.formBuilderService.buildForm(definition));
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }
}
