import { TestBed } from '@angular/core/testing';

import { QuesionService } from './quesion.service';

describe('QuesionService', () => {
  let service: QuesionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuesionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
