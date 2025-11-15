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

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { QuestionnaireFormBuilderService } from '../questionnaire-form-builder.service';
import { QuestionnaireDefinition } from '../questionnaire.model';
import { QuestionnaireService } from '../questionnaire.service';
import { WelfareQuestionnaireComponent } from './welfare-questionnaire.component';

class QuestionnaireServiceStub {
  fetchQuestionnaire = jest.fn();
  submitQuestionnaire = jest.fn();
}

describe('WelfareQuestionnaireComponent', () => {
  let fixture: ComponentFixture<WelfareQuestionnaireComponent>;
  let component: WelfareQuestionnaireComponent;
  let questionnaireService: QuestionnaireServiceStub;

  const definition: QuestionnaireDefinition = {
    id: 'welfare-membership',
    title: 'Welfare Membership',
    sections: [
      {
        id: 'memberDetails',
        title: 'Member details',
        questions: [
          { id: 'firstName', label: 'First name', type: 'text', required: true },
          { id: 'phone', label: 'Phone number', type: 'tel', validation: { pattern: '^[0-9]+$' } },
        ],
      },
      {
        id: 'children',
        title: 'Children',
        repeatable: true,
        minimumEntries: 1,
        questions: [{ id: 'childName', label: 'Child name', type: 'text', required: true }],
      },
    ],
  };

  beforeEach(async () => {
    questionnaireService = new QuestionnaireServiceStub();
    questionnaireService.fetchQuestionnaire.mockReturnValue(of(definition));
    questionnaireService.submitQuestionnaire.mockReturnValue(of({}));

    await TestBed.configureTestingModule({
      imports: [WelfareQuestionnaireComponent],
      providers: [QuestionnaireFormBuilderService, { provide: QuestionnaireService, useValue: questionnaireService }],
    }).compileComponents();

    fixture = TestBed.createComponent(WelfareQuestionnaireComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load questionnaire definition and build form', () => {
    expect(questionnaireService.fetchQuestionnaire).toHaveBeenCalledWith('welfare-membership');
    expect(component.questionnaire()).toEqual(definition);
    expect(component.form()).not.toBeNull();
  });

  it('should add entries to repeatable sections', () => {
    const form = component.form();
    const section = definition.sections[1];

    expect(form).not.toBeNull();
    const array = component.sectionArray(section.id);
    expect(array?.length).toBe(1);

    component.addSection(section);
    expect(component.sectionArray(section.id)?.length).toBe(2);
  });

  it('should submit questionnaire values', () => {
    const form = component.form();
    expect(form).not.toBeNull();

    form?.get('memberDetails.firstName')?.setValue('Jon');
    form?.get('memberDetails.phone')?.setValue('1234567');
    (component.sectionArray('children')?.at(0) as any).get('childName')?.setValue('Jane');

    component.submit();

    expect(questionnaireService.submitQuestionnaire).toHaveBeenCalledWith(
      'welfare-membership',
      expect.objectContaining({
        questionnaireId: 'welfare-membership',
      }),
    );
  });

  it('should handle load errors gracefully', () => {
    questionnaireService.fetchQuestionnaire.mockReturnValueOnce(throwError(() => new Error('fail')));

    const errorFixture = TestBed.createComponent(WelfareQuestionnaireComponent);
    errorFixture.detectChanges();

    expect(errorFixture.componentInstance.loading()).toBe(false);
    expect(errorFixture.componentInstance.form()).toBeNull();
  });
});
