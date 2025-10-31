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

import { Component, DebugElement, Type } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { SortDirective } from './sort.directive';
import { SortState, sortStateSignal } from './sort-state';

@Component({
  standalone: true,
  imports: [SortDirective],
  template: `
    <table>
      <thead>
        <tr jhiSort [sortState]="sortState" (sortChange)="transition($event)"></tr>
      </thead>
    </table>
  `,
})
class TestSortDirectiveComponent {
  sortState = sortStateSignal({ predicate: 'ID' });
  transition = jest.fn().mockImplementation((sortState: SortState) => {
    this.sortState.set(sortState);
  });
}

describe('Directive: SortDirective', () => {
  let component: TestSortDirectiveComponent;
  let fixture: ComponentFixture<TestSortDirectiveComponent>;
  let tableRow: DebugElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestSortDirectiveComponent],
    });
    fixture = TestBed.createComponent(TestSortDirectiveComponent);
    component = fixture.componentInstance;
    tableRow = fixture.debugElement.query(By.directive(SortDirective));
  });

  it('should invoke sortChange function', () => {
    // GIVEN
    const sortDirective = tableRow.injector.get(SortDirective as Type<SortDirective>);

    // WHEN
    fixture.detectChanges();
    sortDirective.sort('ID');

    // THEN
    expect(component.transition).toHaveBeenCalledTimes(1);
    expect(component.transition).toHaveBeenCalledWith({ predicate: 'ID', order: 'asc' });
  });

  it('should change sort order to descending, neutral when same field is sorted again', () => {
    // GIVEN
    const sortDirective = tableRow.injector.get(SortDirective as Type<SortDirective>);

    // WHEN
    fixture.detectChanges();
    sortDirective.sort('ID');
    // sort again
    sortDirective.sort('ID');
    // sort again
    sortDirective.sort('ID');

    // THEN
    expect(component.transition).toHaveBeenCalledTimes(3);
    expect(component.transition).toHaveBeenNthCalledWith(1, { predicate: 'ID', order: 'asc' });
    expect(component.transition).toHaveBeenNthCalledWith(2, { predicate: 'ID', order: 'desc' });
    expect(component.transition).toHaveBeenNthCalledWith(3, { predicate: 'ID', order: 'asc' });
  });

  it('should change sort order to ascending when different field is sorted', () => {
    // GIVEN
    const sortDirective = tableRow.injector.get(SortDirective as Type<SortDirective>);

    // WHEN
    fixture.detectChanges();
    sortDirective.sort('ID');
    // sort again
    sortDirective.sort('NAME');

    // THEN
    expect(component.transition).toHaveBeenCalledTimes(2);
    expect(component.transition).toHaveBeenNthCalledWith(1, { predicate: 'ID', order: 'asc' });
    expect(component.transition).toHaveBeenNthCalledWith(2, { predicate: 'NAME', order: 'asc' });
  });
});
