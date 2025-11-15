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

import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { QuestionnaireDefinition } from './questionnaire.model';
import { QuestionnaireService } from './questionnaire.service';

describe('QuestionnaireService', () => {
  let service: QuestionnaireService;
  let httpMock: HttpTestingController;

  const questionnaireId = 'welfare-membership';
  const definition: QuestionnaireDefinition = {
    id: questionnaireId,
    title: 'Welfare Membership',
    sections: [],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(QuestionnaireService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch questionnaire definitions', () => {
    let received: QuestionnaireDefinition | null = null;

    service.fetchQuestionnaire(questionnaireId).subscribe(response => (received = response));

    const request = httpMock.expectOne({ method: 'GET' });
    expect(request.request.url).toBe(`api/public/questionnaires/${questionnaireId}`);
    request.flush(definition);

    expect(received).toEqual(definition);
  });

  it('should submit questionnaire payloads', () => {
    const payload = { questionnaireId, responses: { firstName: 'Jon' } };
    let completed = false;

    service.submitQuestionnaire(questionnaireId, payload).subscribe(() => (completed = true));

    const request = httpMock.expectOne({ method: 'POST' });
    expect(request.request.url).toBe(`api/public/questionnaires/${questionnaireId}/submissions`);
    request.flush({});

    expect(completed).toBe(true);
  });
});
