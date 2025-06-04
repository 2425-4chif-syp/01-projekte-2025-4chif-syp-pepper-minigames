import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddStepComponent } from './add-step.component';

describe('AddStepComponent', () => {
  let component: AddStepComponent;
  let fixture: ComponentFixture<AddStepComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddStepComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddStepComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
