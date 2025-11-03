import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModelForm } from './model-form';

describe('ModelForm', () => {
  let component: ModelForm;
  let fixture: ComponentFixture<ModelForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModelForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModelForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
