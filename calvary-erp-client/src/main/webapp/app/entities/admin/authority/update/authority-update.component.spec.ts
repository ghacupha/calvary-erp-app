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
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { AuthorityService } from '../service/authority.service';
import { IAuthority } from '../authority.model';
import { AuthorityFormService } from './authority-form.service';

import { AuthorityUpdateComponent } from './authority-update.component';

describe('Authority Management Update Component', () => {
  let comp: AuthorityUpdateComponent;
  let fixture: ComponentFixture<AuthorityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let authorityFormService: AuthorityFormService;
  let authorityService: AuthorityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AuthorityUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AuthorityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AuthorityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    authorityFormService = TestBed.inject(AuthorityFormService);
    authorityService = TestBed.inject(AuthorityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const authority: IAuthority = { name: 'CBA' };

      activatedRoute.data = of({ authority });
      comp.ngOnInit();

      expect(comp.authority).toEqual(authority);
    });
  });

  describe('save', () => {
    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAuthority>>();
      const authority = { name: 'ABC' };
      jest.spyOn(authorityFormService, 'getAuthority').mockReturnValue({ name: null });
      jest.spyOn(authorityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ authority: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: authority }));
      saveSubject.complete();

      // THEN
      expect(authorityFormService.getAuthority).toHaveBeenCalled();
      expect(authorityService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });
  });
});
