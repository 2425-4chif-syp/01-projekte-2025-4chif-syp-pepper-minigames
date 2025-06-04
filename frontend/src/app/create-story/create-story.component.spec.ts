import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateStoryComponent } from './create-story.component';

describe('CreateStoryComponent', () => {
  let component: CreateStoryComponent;
  let fixture: ComponentFixture<CreateStoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateStoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateStoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
