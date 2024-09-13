import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IInstitution } from 'app/entities/institution/institution.model';
import { InstitutionService } from 'app/entities/institution/service/institution.service';
import { InstitutionalSubscriptionService } from '../service/institutional-subscription.service';
import { IInstitutionalSubscription } from '../institutional-subscription.model';
import { InstitutionalSubscriptionFormService } from './institutional-subscription-form.service';

import { InstitutionalSubscriptionUpdateComponent } from './institutional-subscription-update.component';

describe('InstitutionalSubscription Management Update Component', () => {
  let comp: InstitutionalSubscriptionUpdateComponent;
  let fixture: ComponentFixture<InstitutionalSubscriptionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let institutionalSubscriptionFormService: InstitutionalSubscriptionFormService;
  let institutionalSubscriptionService: InstitutionalSubscriptionService;
  let institutionService: InstitutionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InstitutionalSubscriptionUpdateComponent],
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
      .overrideTemplate(InstitutionalSubscriptionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InstitutionalSubscriptionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    institutionalSubscriptionFormService = TestBed.inject(InstitutionalSubscriptionFormService);
    institutionalSubscriptionService = TestBed.inject(InstitutionalSubscriptionService);
    institutionService = TestBed.inject(InstitutionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Institution query and add missing value', () => {
      const institutionalSubscription: IInstitutionalSubscription = { id: 456 };
      const institution: IInstitution = { id: 15196 };
      institutionalSubscription.institution = institution;

      const institutionCollection: IInstitution[] = [{ id: 26420 }];
      jest.spyOn(institutionService, 'query').mockReturnValue(of(new HttpResponse({ body: institutionCollection })));
      const additionalInstitutions = [institution];
      const expectedCollection: IInstitution[] = [...additionalInstitutions, ...institutionCollection];
      jest.spyOn(institutionService, 'addInstitutionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ institutionalSubscription });
      comp.ngOnInit();

      expect(institutionService.query).toHaveBeenCalled();
      expect(institutionService.addInstitutionToCollectionIfMissing).toHaveBeenCalledWith(
        institutionCollection,
        ...additionalInstitutions.map(expect.objectContaining),
      );
      expect(comp.institutionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const institutionalSubscription: IInstitutionalSubscription = { id: 456 };
      const institution: IInstitution = { id: 3170 };
      institutionalSubscription.institution = institution;

      activatedRoute.data = of({ institutionalSubscription });
      comp.ngOnInit();

      expect(comp.institutionsSharedCollection).toContain(institution);
      expect(comp.institutionalSubscription).toEqual(institutionalSubscription);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstitutionalSubscription>>();
      const institutionalSubscription = { id: 123 };
      jest.spyOn(institutionalSubscriptionFormService, 'getInstitutionalSubscription').mockReturnValue(institutionalSubscription);
      jest.spyOn(institutionalSubscriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ institutionalSubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: institutionalSubscription }));
      saveSubject.complete();

      // THEN
      expect(institutionalSubscriptionFormService.getInstitutionalSubscription).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(institutionalSubscriptionService.update).toHaveBeenCalledWith(expect.objectContaining(institutionalSubscription));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstitutionalSubscription>>();
      const institutionalSubscription = { id: 123 };
      jest.spyOn(institutionalSubscriptionFormService, 'getInstitutionalSubscription').mockReturnValue({ id: null });
      jest.spyOn(institutionalSubscriptionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ institutionalSubscription: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: institutionalSubscription }));
      saveSubject.complete();

      // THEN
      expect(institutionalSubscriptionFormService.getInstitutionalSubscription).toHaveBeenCalled();
      expect(institutionalSubscriptionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstitutionalSubscription>>();
      const institutionalSubscription = { id: 123 };
      jest.spyOn(institutionalSubscriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ institutionalSubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(institutionalSubscriptionService.update).toHaveBeenCalled();
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
