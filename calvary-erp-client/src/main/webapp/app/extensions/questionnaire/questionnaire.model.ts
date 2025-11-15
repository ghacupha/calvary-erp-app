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

export type QuestionnaireInputType =
  | 'text'
  | 'number'
  | 'tel'
  | 'email'
  | 'date'
  | 'textarea'
  | 'select';

export interface QuestionnaireOption {
  value: string;
  label: string;
}

export interface QuestionnaireValidationRules {
  min?: number;
  max?: number;
  minLength?: number;
  maxLength?: number;
  pattern?: string;
  customMessage?: string;
}

export interface QuestionnaireQuestion {
  id: string;
  label: string;
  type: QuestionnaireInputType;
  placeholder?: string;
  hint?: string;
  required?: boolean;
  defaultValue?: unknown;
  options?: QuestionnaireOption[];
  validation?: QuestionnaireValidationRules;
}

export interface QuestionnaireSection {
  id: string;
  title: string;
  description?: string;
  repeatable?: boolean;
  minimumEntries?: number;
  maximumEntries?: number;
  questions: QuestionnaireQuestion[];
}

export interface QuestionnaireDefinition {
  id: string;
  title: string;
  description?: string;
  submitLabel?: string;
  sections: QuestionnaireSection[];
}

export interface QuestionnaireSubmissionPayload {
  questionnaireId: string;
  responses: unknown;
}
