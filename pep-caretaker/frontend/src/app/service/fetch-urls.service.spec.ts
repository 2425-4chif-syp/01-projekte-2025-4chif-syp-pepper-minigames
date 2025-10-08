import { TestBed } from '@angular/core/testing';

import { FetchUrlsService } from './fetch-urls.service';

describe('FetchUrlsService', () => {
  let service: FetchUrlsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FetchUrlsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
