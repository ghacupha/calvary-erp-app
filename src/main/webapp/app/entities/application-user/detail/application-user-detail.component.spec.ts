import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ApplicationUserDetailComponent } from './application-user-detail.component';

describe('ApplicationUser Management Detail Component', () => {
  let comp: ApplicationUserDetailComponent;
  let fixture: ComponentFixture<ApplicationUserDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApplicationUserDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./application-user-detail.component').then(m => m.ApplicationUserDetailComponent),
              resolve: { applicationUser: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ApplicationUserDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationUserDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load applicationUser on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ApplicationUserDetailComponent);

      // THEN
      expect(instance.applicationUser()).toEqual(expect.objectContaining({ id: 123 }));
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
