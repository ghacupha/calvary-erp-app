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

import { FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TestBed } from '@angular/core/testing';

import { QuestionnaireDefinition } from './questionnaire.model';
import { QuestionnaireFormBuilderService } from './questionnaire-form-builder.service';

describe('QuestionnaireFormBuilderService', () => {
  let service: QuestionnaireFormBuilderService;

  const definition: QuestionnaireDefinition = {
    id: 'welfare-membership',
    title: 'Welfare Membership',
    sections: [
      {
        id: 'memberDetails',
        title: 'Member details',
        questions: [
          { id: 'firstName', label: 'First name', type: 'text', required: true },
          { id: 'age', label: 'Age', type: 'number', validation: { min: 18 } },
        ],
      },
      {
        id: 'children',
        title: 'Children',
        repeatable: true,
        minimumEntries: 1,
        questions: [{ id: 'childName', label: 'Child name', type: 'text' }],
      },
    ],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
    });

    service = TestBed.inject(QuestionnaireFormBuilderService);
  });

  it('should create form groups for each section', () => {
    const form = service.buildForm(definition);

    expect(form instanceof FormGroup).toBe(true);
    expect(form.get('memberDetails')).toBeInstanceOf(FormGroup);
    expect(form.get('children')).toBeInstanceOf(FormArray);
  });

  it('should apply validators to controls', () => {
    const form = service.buildForm(definition);
    const memberDetails = form.get('memberDetails') as FormGroup;

    const firstName = memberDetails.get('firstName');
    const age = memberDetails.get('age');

    firstName?.setValue('');
    age?.setValue(12);

    expect(firstName?.valid).toBe(false);
    expect(age?.valid).toBe(false);
  });

  it('should add and remove repeatable section instances', () => {
    const form = service.buildForm(definition);
    const repeatableSection = definition.sections[1];
    const array = form.get('children') as FormArray;

    expect(array.length).toBe(1);

    service.addSectionItem(form, repeatableSection);
    expect(array.length).toBe(2);

    service.removeSectionItem(form, 'children', 0, repeatableSection.minimumEntries);
    expect(array.length).toBe(1);
  });
});
