import { TestBed } from '@angular/core/testing';

import { Boat } from './boat';

describe('Boat', () => {
  let service: Boat;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Boat);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
