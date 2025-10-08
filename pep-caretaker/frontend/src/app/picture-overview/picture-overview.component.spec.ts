import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PictureOverviewComponent } from './picture-overview.component';

describe('PictureOverviewComponent', () => {
  let component: PictureOverviewComponent;
  let fixture: ComponentFixture<PictureOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PictureOverviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PictureOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
