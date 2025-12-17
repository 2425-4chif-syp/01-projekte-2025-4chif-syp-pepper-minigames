import { TestBed } from '@angular/core/testing';

import { ResidentServiceService } from './resident-service.service';

describe('ResidentServiceService', () => {
  let service: ResidentServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResidentServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
