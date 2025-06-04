import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoveSelectorComponent } from './move-selector.component';

describe('MoveSelectorComponent', () => {
  let component: MoveSelectorComponent;
  let fixture: ComponentFixture<MoveSelectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoveSelectorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoveSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
