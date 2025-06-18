import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResidentDetailsComponent } from './resident-details.component';

describe('ResidentDetailsComponent', () => {
  let component: ResidentDetailsComponent;
  let fixture: ComponentFixture<ResidentDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResidentDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResidentDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
