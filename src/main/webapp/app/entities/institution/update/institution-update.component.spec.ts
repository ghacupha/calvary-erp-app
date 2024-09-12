import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { InstitutionService } from '../service/institution.service';
import { IInstitution } from '../institution.model';
import { InstitutionFormService } from './institution-form.service';

import { InstitutionUpdateComponent } from './institution-update.component';

describe('Institution Management Update Component', () => {
  let comp: InstitutionUpdateComponent;
  let fixture: ComponentFixture<InstitutionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let institutionFormService: InstitutionFormService;
  let institutionService: InstitutionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InstitutionUpdateComponent],
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
      .overrideTemplate(InstitutionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InstitutionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    institutionFormService = TestBed.inject(InstitutionFormService);
    institutionService = TestBed.inject(InstitutionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Institution query and add missing value', () => {
      const institution: IInstitution = { id: 456 };
      const parentInstitution: IInstitution = { id: 26579 };
      institution.parentInstitution = parentInstitution;

      const institutionCollection: IInstitution[] = [{ id: 20002 }];
      jest.spyOn(institutionService, 'query').mockReturnValue(of(new HttpResponse({ body: institutionCollection })));
      const additionalInstitutions = [parentInstitution];
      const expectedCollection: IInstitution[] = [...additionalInstitutions, ...institutionCollection];
      jest.spyOn(institutionService, 'addInstitutionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ institution });
      comp.ngOnInit();

      expect(institutionService.query).toHaveBeenCalled();
      expect(institutionService.addInstitutionToCollectionIfMissing).toHaveBeenCalledWith(
        institutionCollection,
        ...additionalInstitutions.map(expect.objectContaining),
      );
      expect(comp.institutionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const institution: IInstitution = { id: 456 };
      const parentInstitution: IInstitution = { id: 24607 };
      institution.parentInstitution = parentInstitution;

      activatedRoute.data = of({ institution });
      comp.ngOnInit();

      expect(comp.institutionsSharedCollection).toContain(parentInstitution);
      expect(comp.institution).toEqual(institution);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstitution>>();
      const institution = { id: 123 };
      jest.spyOn(institutionFormService, 'getInstitution').mockReturnValue(institution);
      jest.spyOn(institutionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ institution });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: institution }));
      saveSubject.complete();

      // THEN
      expect(institutionFormService.getInstitution).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(institutionService.update).toHaveBeenCalledWith(expect.objectContaining(institution));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstitution>>();
      const institution = { id: 123 };
      jest.spyOn(institutionFormService, 'getInstitution').mockReturnValue({ id: null });
      jest.spyOn(institutionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ institution: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: institution }));
      saveSubject.complete();

      // THEN
      expect(institutionFormService.getInstitution).toHaveBeenCalled();
      expect(institutionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstitution>>();
      const institution = { id: 123 };
      jest.spyOn(institutionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ institution });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(institutionService.update).toHaveBeenCalled();
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
