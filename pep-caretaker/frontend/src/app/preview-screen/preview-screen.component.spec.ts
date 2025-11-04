import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewScreenComponent } from './preview-screen.component';

describe('PreviewScreenComponent', () => {
  let component: PreviewScreenComponent;
  let fixture: ComponentFixture<PreviewScreenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreviewScreenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
