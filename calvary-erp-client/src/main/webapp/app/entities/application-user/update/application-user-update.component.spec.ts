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

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IInstitution } from 'app/entities/institution/institution.model';
import { InstitutionService } from 'app/entities/institution/service/institution.service';
import { IApplicationUser } from '../application-user.model';
import { ApplicationUserService } from '../service/application-user.service';
import { ApplicationUserFormService } from './application-user-form.service';

import { ApplicationUserUpdateComponent } from './application-user-update.component';

describe('ApplicationUser Management Update Component', () => {
  let comp: ApplicationUserUpdateComponent;
  let fixture: ComponentFixture<ApplicationUserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let applicationUserFormService: ApplicationUserFormService;
  let applicationUserService: ApplicationUserService;
  let userService: UserService;
  let institutionService: InstitutionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ApplicationUserUpdateComponent],
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
      .overrideTemplate(ApplicationUserUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ApplicationUserUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    applicationUserFormService = TestBed.inject(ApplicationUserFormService);
    applicationUserService = TestBed.inject(ApplicationUserService);
    userService = TestBed.inject(UserService);
    institutionService = TestBed.inject(InstitutionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const applicationUser: IApplicationUser = { id: 456 };
      const systemUser: IUser = { id: 9076 };
      applicationUser.systemUser = systemUser;

      const userCollection: IUser[] = [{ id: 15701 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [systemUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ applicationUser });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Institution query and add missing value', () => {
      const applicationUser: IApplicationUser = { id: 456 };
      const institution: IInstitution = { id: 9357 };
      applicationUser.institution = institution;

      const institutionCollection: IInstitution[] = [{ id: 31040 }];
      jest.spyOn(institutionService, 'query').mockReturnValue(of(new HttpResponse({ body: institutionCollection })));
      const additionalInstitutions = [institution];
      const expectedCollection: IInstitution[] = [...additionalInstitutions, ...institutionCollection];
      jest.spyOn(institutionService, 'addInstitutionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ applicationUser });
      comp.ngOnInit();

      expect(institutionService.query).toHaveBeenCalled();
      expect(institutionService.addInstitutionToCollectionIfMissing).toHaveBeenCalledWith(
        institutionCollection,
        ...additionalInstitutions.map(expect.objectContaining),
      );
      expect(comp.institutionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const applicationUser: IApplicationUser = { id: 456 };
      const systemUser: IUser = { id: 2666 };
      applicationUser.systemUser = systemUser;
      const institution: IInstitution = { id: 767 };
      applicationUser.institution = institution;

      activatedRoute.data = of({ applicationUser });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(systemUser);
      expect(comp.institutionsSharedCollection).toContain(institution);
      expect(comp.applicationUser).toEqual(applicationUser);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationUser>>();
      const applicationUser = { id: 123 };
      jest.spyOn(applicationUserFormService, 'getApplicationUser').mockReturnValue(applicationUser);
      jest.spyOn(applicationUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: applicationUser }));
      saveSubject.complete();

      // THEN
      expect(applicationUserFormService.getApplicationUser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(applicationUserService.update).toHaveBeenCalledWith(expect.objectContaining(applicationUser));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationUser>>();
      const applicationUser = { id: 123 };
      jest.spyOn(applicationUserFormService, 'getApplicationUser').mockReturnValue({ id: null });
      jest.spyOn(applicationUserService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationUser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: applicationUser }));
      saveSubject.complete();

      // THEN
      expect(applicationUserFormService.getApplicationUser).toHaveBeenCalled();
      expect(applicationUserService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationUser>>();
      const applicationUser = { id: 123 };
      jest.spyOn(applicationUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(applicationUserService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareInstitution', () => {
      it('Should forward to institutionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(institutionService, 'compareInstitution');
        comp.compareInstitution(entity, entity2);
        expect(institutionService.compareInstitution).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
