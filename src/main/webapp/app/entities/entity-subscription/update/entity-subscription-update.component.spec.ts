import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { EntitySubscriptionService } from '../service/entity-subscription.service';
import { IEntitySubscription } from '../entity-subscription.model';
import { EntitySubscriptionFormService } from './entity-subscription-form.service';

import { EntitySubscriptionUpdateComponent } from './entity-subscription-update.component';

describe('EntitySubscription Management Update Component', () => {
  let comp: EntitySubscriptionUpdateComponent;
  let fixture: ComponentFixture<EntitySubscriptionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let entitySubscriptionFormService: EntitySubscriptionFormService;
  let entitySubscriptionService: EntitySubscriptionService;

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

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const entitySubscription: IEntitySubscription = { id: 456 };

      activatedRoute.data = of({ entitySubscription });
      comp.ngOnInit();

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
});
