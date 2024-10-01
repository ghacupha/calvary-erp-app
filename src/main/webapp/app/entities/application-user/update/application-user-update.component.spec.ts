import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ApplicationUserService } from '../service/application-user.service';
import { IApplicationUser } from '../application-user.model';
import { ApplicationUserFormService } from './application-user-form.service';

import { ApplicationUserUpdateComponent } from './application-user-update.component';

describe('ApplicationUser Management Update Component', () => {
  let comp: ApplicationUserUpdateComponent;
  let fixture: ComponentFixture<ApplicationUserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let applicationUserFormService: ApplicationUserFormService;
  let applicationUserService: ApplicationUserService;
  let userService: UserService;

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

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const applicationUser: IApplicationUser = { id: 456 };
      const systemUser: IUser = { id: 15003 };
      applicationUser.systemUser = systemUser;

      const userCollection: IUser[] = [{ id: 8328 }];
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

    it('Should update editForm', () => {
      const applicationUser: IApplicationUser = { id: 456 };
      const systemUser: IUser = { id: 30496 };
      applicationUser.systemUser = systemUser;

      activatedRoute.data = of({ applicationUser });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(systemUser);
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
  });
});
