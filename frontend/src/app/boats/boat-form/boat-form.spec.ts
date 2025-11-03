import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoatForm } from './boat-form';

describe('BoatForm', () => {
  let component: BoatForm;
  let fixture: ComponentFixture<BoatForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BoatForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoatForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
