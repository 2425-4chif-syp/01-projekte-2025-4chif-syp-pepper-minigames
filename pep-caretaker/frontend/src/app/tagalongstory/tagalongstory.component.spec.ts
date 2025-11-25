import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagalongstoryComponent } from './tagalongstory.component';

describe('TagalongstoryComponent', () => {
  let component: TagalongstoryComponent;
  let fixture: ComponentFixture<TagalongstoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagalongstoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TagalongstoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
