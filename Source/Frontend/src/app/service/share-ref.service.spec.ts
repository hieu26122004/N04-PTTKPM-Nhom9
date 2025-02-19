import { TestBed } from '@angular/core/testing';

import { ShareRefService } from './share-ref.service';

describe('ShareRefService', () => {
  let service: ShareRefService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShareRefService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
