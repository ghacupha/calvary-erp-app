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

import { IInstitution } from 'app/entities/institution/institution.model';
import { InstitutionService } from 'app/entities/institution/service/institution.service';
import { EntitySubscriptionService } from '../../../../entities/entity-subscription/service/entity-subscription.service';
import { IEntitySubscription } from '../entity-subscription.model';
import { EntitySubscriptionFormService } from './entity-subscription-form.service';

import { EntitySubscriptionUpdateComponent } from './entity-subscription-update.component';

describe('EntitySubscription Management Update Component', () => {
  let comp: EntitySubscriptionUpdateComponent;
  let fixture: ComponentFixture<EntitySubscriptionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let entitySubscriptionFormService: EntitySubscriptionFormService;
  let entitySubscriptionService: EntitySubscriptionService;
  let institutionService: InstitutionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [EntitySubscriptionUpdateComponent],
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
      .overrideTemplate(EntitySubscriptionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EntitySubscriptionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    entitySubscriptionFormService = TestBed.inject(EntitySubscriptionFormService);
    entitySubscriptionService = TestBed.inject(EntitySubscriptionService);
    institutionService = TestBed.inject(InstitutionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Institution query and add missing value', () => {
      const entitySubscription: IEntitySubscription = { id: 456 };
      const institution: IInstitution = { id: 32560 };
      entitySubscription.institution = institution;

      const institutionCollection: IInstitution[] = [{ id: 7499 }];
      jest.spyOn(institutionService, 'query').mockReturnValue(of(new HttpResponse({ body: institutionCollection })));
      const additionalInstitutions = [institution];
      const expectedCollection: IInstitution[] = [...additionalInstitutions, ...institutionCollection];
      jest.spyOn(institutionService, 'addInstitutionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ entitySubscription });
      comp.ngOnInit();

      expect(institutionService.query).toHaveBeenCalled();
      expect(institutionService.addInstitutionToCollectionIfMissing).toHaveBeenCalledWith(
        institutionCollection,
        ...additionalInstitutions.map(expect.objectContaining),
      );
      expect(comp.institutionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const entitySubscription: IEntitySubscription = { id: 456 };
      const institution: IInstitution = { id: 26464 };
      entitySubscription.institution = institution;

      activatedRoute.data = of({ entitySubscription });
      comp.ngOnInit();

      expect(comp.institutionsSharedCollection).toContain(institution);
      expect(comp.entitySubscription).toEqual(entitySubscription);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEntitySubscription>>();
      const entitySubscription = { id: 123 };
      jest.spyOn(entitySubscriptionFormService, 'getEntitySubscription').mockReturnValue(entitySubscription);
      jest.spyOn(entitySubscriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ entitySubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: entitySubscription }));
      saveSubject.complete();

      // THEN
      expect(entitySubscriptionFormService.getEntitySubscription).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(entitySubscriptionService.update).toHaveBeenCalledWith(expect.objectContaining(entitySubscription));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEntitySubscription>>();
      const entitySubscription = { id: 123 };
      jest.spyOn(entitySubscriptionFormService, 'getEntitySubscription').mockReturnValue({ id: null });
      jest.spyOn(entitySubscriptionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ entitySubscription: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: entitySubscription }));
      saveSubject.complete();

      // THEN
      expect(entitySubscriptionFormService.getEntitySubscription).toHaveBeenCalled();
      expect(entitySubscriptionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEntitySubscription>>();
      const entitySubscription = { id: 123 };
      jest.spyOn(entitySubscriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ entitySubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(entitySubscriptionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
