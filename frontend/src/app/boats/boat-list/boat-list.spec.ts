import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoatList } from './boat-list';

describe('BoatList', () => {
  let component: BoatList;
  let fixture: ComponentFixture<BoatList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BoatList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoatList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
