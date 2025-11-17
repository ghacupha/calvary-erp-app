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
import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';
import { StateStorageService } from './state-storage.service';

describe('Auth JWT', () => {
  let service: AuthServerProvider;
  let httpMock: HttpTestingController;
  let mockStorageService: StateStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    mockStorageService = TestBed.inject(StateStorageService);
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(AuthServerProvider);
  });

  describe('Get Token', () => {
    it('should return empty token if not found in local storage nor session storage', () => {
      const result = service.getToken();
      expect(result).toEqual('');
    });

    it('should return token from session storage if local storage is empty', () => {
      sessionStorage.setItem('jhi-authenticationToken', JSON.stringify('sessionStorageToken'));
      const result = service.getToken();
      expect(result).toEqual('sessionStorageToken');
    });

    it('should return token from localstorage storage', () => {
      localStorage.setItem('jhi-authenticationToken', JSON.stringify('localStorageToken'));
      const result = service.getToken();
      expect(result).toEqual('localStorageToken');
    });
  });

  describe('Login', () => {
    it('should clear session storage and save in local storage when rememberMe is true', () => {
      // GIVEN
      mockStorageService.storeAuthenticationToken = jest.fn();

      // WHEN
      service.login({ username: 'John', password: '123', rememberMe: true }).subscribe();
      httpMock.expectOne('api/authenticate').flush({ id_token: '1' });

      // THEN
      httpMock.verify();
      expect(mockStorageService.storeAuthenticationToken).toHaveBeenCalledWith('1', true);
    });

    it('should clear local storage and save in session storage when rememberMe is false', () => {
      // GIVEN
      mockStorageService.storeAuthenticationToken = jest.fn();

      // WHEN
      service.login({ username: 'John', password: '123', rememberMe: false }).subscribe();
      httpMock.expectOne('api/authenticate').flush({ id_token: '1' });

      // THEN
      httpMock.verify();
      expect(mockStorageService.storeAuthenticationToken).toHaveBeenCalledWith('1', false);
    });
  });

  describe('Logout', () => {
    it('should clear storage', () => {
      // GIVEN
      mockStorageService.clearAuthenticationToken = jest.fn();

      // WHEN
      service.logout().subscribe();

      // THEN
      expect(mockStorageService.clearAuthenticationToken).toHaveBeenCalled();
    });
  });
});
