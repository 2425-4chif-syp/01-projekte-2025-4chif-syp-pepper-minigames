import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonEntryComponent } from './person-entry.component';

describe('PersonEntryComponent', () => {
  let component: PersonEntryComponent;
  let fixture: ComponentFixture<PersonEntryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonEntryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PersonEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
