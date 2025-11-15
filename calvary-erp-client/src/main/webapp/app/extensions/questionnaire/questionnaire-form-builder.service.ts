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

import { Injectable, inject } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';

import { QuestionnaireDefinition, QuestionnaireQuestion, QuestionnaireSection } from './questionnaire.model';

@Injectable({ providedIn: 'root' })
export class QuestionnaireFormBuilderService {
  private readonly formBuilder = inject(FormBuilder);

  buildForm(questionnaire: QuestionnaireDefinition): FormGroup {
    const sectionControls: Record<string, FormGroup | FormArray> = {};

    questionnaire.sections.forEach(section => {
      if (section.repeatable) {
        sectionControls[section.id] = this.formBuilder.array([this.createSectionGroup(section)]);
      } else {
        sectionControls[section.id] = this.createSectionGroup(section);
      }
    });

    return this.formBuilder.group(sectionControls);
  }

  addSectionItem(form: FormGroup, section: QuestionnaireSection): void {
    const array = form.get(section.id);

    if (!(array instanceof FormArray)) {
      throw new Error(`Section '${section.id}' is not repeatable.`);
    }

    if (section.maximumEntries !== undefined && array.length >= section.maximumEntries) {
      return;
    }

    array.push(this.createSectionGroup(section));
  }

  removeSectionItem(form: FormGroup, sectionId: string, index: number, minimumEntries = 1): void {
    const array = form.get(sectionId);

    if (!(array instanceof FormArray)) {
      throw new Error(`Section '${sectionId}' is not repeatable.`);
    }

    if (array.length <= minimumEntries) {
      return;
    }

    array.removeAt(index);
  }

  private createSectionGroup(section: QuestionnaireSection): FormGroup {
    const controls: Record<string, FormControl> = {};

    section.questions.forEach(question => {
      controls[question.id] = this.formBuilder.control(question.defaultValue ?? null, this.resolveValidators(question));
    });

    return this.formBuilder.group(controls);
  }

  private resolveValidators(question: QuestionnaireQuestion): ValidatorFn[] {
    const validators: ValidatorFn[] = [];

    if (question.required) {
      validators.push(Validators.required);
    }

    if (question.validation?.min !== undefined) {
      validators.push(Validators.min(question.validation.min));
    }

    if (question.validation?.max !== undefined) {
      validators.push(Validators.max(question.validation.max));
    }

    if (question.validation?.minLength !== undefined) {
      validators.push(Validators.minLength(question.validation.minLength));
    }

    if (question.validation?.maxLength !== undefined) {
      validators.push(Validators.maxLength(question.validation.maxLength));
    }

    if (question.validation?.pattern !== undefined) {
      validators.push(Validators.pattern(question.validation.pattern));
    }

    return validators;
  }
}
