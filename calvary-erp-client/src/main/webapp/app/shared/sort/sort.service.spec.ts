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

import { SortService } from './sort.service';

describe('sort state', () => {
  const service = new SortService();

  describe('parseSortParam', () => {
    it('should accept undefined value', () => {
      const sortState = service.parseSortParam(undefined);
      expect(sortState).toEqual({});
    });
    it('should accept empty string', () => {
      const sortState = service.parseSortParam('');
      expect(sortState).toEqual({});
    });
    it('should accept predicate only string', () => {
      const sortState = service.parseSortParam('predicate');
      expect(sortState).toEqual({ predicate: 'predicate' });
    });
    it('should accept predicate and ASC string', () => {
      const sortState = service.parseSortParam('predicate,asc');
      expect(sortState).toEqual({ predicate: 'predicate', order: 'asc' });
    });
    it('should accept predicate and DESC string', () => {
      const sortState = service.parseSortParam('predicate,desc');
      expect(sortState).toEqual({ predicate: 'predicate', order: 'desc' });
    });
  });
  describe('buildSortParam', () => {
    it('should accept empty object', () => {
      const sortParam = service.buildSortParam({});
      expect(sortParam).toEqual([]);
    });
    it('should accept object with predicate', () => {
      const sortParam = service.buildSortParam({ predicate: 'column' });
      expect(sortParam).toEqual([]);
    });
    it('should accept object with predicate and asc value', () => {
      const sortParam = service.buildSortParam({ predicate: 'column', order: 'asc' });
      expect(sortParam).toEqual(['column,asc']);
    });
    it('should accept object with predicate and desc value', () => {
      const sortParam = service.buildSortParam({ predicate: 'column', order: 'desc' });
      expect(sortParam).toEqual(['column,desc']);
    });
  });
});
