import { TestBed } from '@angular/core/testing';

import { AttempServiceService } from './attemp-service.service';

describe('AttempServiceService', () => {
  let service: AttempServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AttempServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
