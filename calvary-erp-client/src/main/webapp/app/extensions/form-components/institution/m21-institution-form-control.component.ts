import { Component, EventEmitter, forwardRef, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { IInstitution } from 'app/entities/institution/institution.model';
import { Subject, Observable, of, concat, filter, distinctUntilChanged, debounceTime, tap, switchMap, catchError } from 'rxjs';
import { map } from 'rxjs/operators';
import { ERPInstitutionService } from '../../registration/institution/service/institution.service';
import { NgOptionTemplateDirective, NgSelectComponent } from '@ng-select/ng-select';
import { AsyncPipe, JsonPipe } from '@angular/common';
import { InstitutionOptionViewComponent } from './institution-option-view.component';
import { FormatInstitutionPipe } from './format-institution.pipe';
import HasAnyAuthorityDirective from '../../../shared/auth/has-any-authority.directive';
import SharedModule from '../../../shared/shared.module';

@Component({
  standalone: true,
  selector: 'jhi-m21-institution-form-control',
  templateUrl: './m21-institution-form-control.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => M21InstitutionFormControlComponent),
      multi: true,
    },
  ],
  imports: [
    SharedModule,
    NgSelectComponent,
    FormsModule,
    AsyncPipe,
    InstitutionOptionViewComponent,
    NgOptionTemplateDirective,
    FormatInstitutionPipe,
    HasAnyAuthorityDirective,
    JsonPipe,
  ],
})
export class M21InstitutionFormControlComponent implements OnInit, ControlValueAccessor, OnDestroy {
  @Input() inputValue!: IInstitution;

  @Input() inputControlLabel = '';

  @Input() disabledInput = false;

  @Output() valueSelected: EventEmitter<IInstitution> = new EventEmitter<IInstitution>();

  minAccountLengthTerm = 3;
  valuesLoading = false;
  valueControlInput$ = new Subject<string>();
  valueLookUps$: Observable<IInstitution[]> = of([]);

  protected valueSuggestionService = inject(ERPInstitutionService);
  protected valueService = inject(ERPInstitutionService);

  onChange: any = () => {
    this.getValues();
  };

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  onTouched: any = () => {};

  ngOnInit(): void {
    this.loadValues();
  }

  ngOnDestroy(): void {
    this.valueLookUps$ = of([]);
  }

  loadValues(): void {
    this.valueLookUps$ = concat(
      of([]), // Default empty array for initialization
      this.valueControlInput$.pipe(
        // Filter for valid input values
        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        filter(res => res !== null && res.length >= this.minAccountLengthTerm),
        distinctUntilChanged(), // Ignore duplicate consecutive terms
        debounceTime(800), // Debounce to minimize API calls
        tap(() => (this.valuesLoading = true)), // Show a loading state
        // Perform search and map results to an array of IInstitution
        switchMap(term =>
          this.valueSuggestionService.searchInputs(term).pipe(
            catchError(() => of([])), // Handle search errors gracefully
            switchMap(searchResults =>
              searchResults.length > 0
                ? this.valueService.query({ 'id.in': searchResults.map(result => result.id) }).pipe(
                    // Extract the body from the EntityArrayResponseType
                    map(response => response.body ?? []),
                    catchError(() => of([])), // Handle service errors gracefully
                  )
                : of([]),
            ),
            tap(() => (this.valuesLoading = false)), // Turn off the loading state
          ),
        ),
      ),
    );
  }

  trackValueByFn(item: any): number {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return item.id!;
  }

  writeValue(value: IInstitution): void {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (value) {
      this.inputValue = value;
    }
  }

  getValues(): void {
    this.valueSelected.emit(this.inputValue);
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
}
