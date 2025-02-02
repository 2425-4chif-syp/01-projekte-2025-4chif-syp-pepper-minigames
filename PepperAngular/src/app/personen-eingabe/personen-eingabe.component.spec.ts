import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonenEingabeComponent } from './personen-eingabe.component';

describe('PersonenEingabeComponent', () => {
  let component: PersonenEingabeComponent;
  let fixture: ComponentFixture<PersonenEingabeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonenEingabeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PersonenEingabeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
