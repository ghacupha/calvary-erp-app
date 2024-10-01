import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { EntitySubscriptionDetailComponent } from './entity-subscription-detail.component';

describe('EntitySubscription Management Detail Component', () => {
  let comp: EntitySubscriptionDetailComponent;
  let fixture: ComponentFixture<EntitySubscriptionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntitySubscriptionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./entity-subscription-detail.component').then(m => m.EntitySubscriptionDetailComponent),
              resolve: { entitySubscription: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(EntitySubscriptionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EntitySubscriptionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load entitySubscription on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EntitySubscriptionDetailComponent);

      // THEN
      expect(instance.entitySubscription()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
