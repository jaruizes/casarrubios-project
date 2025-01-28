import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PositionDetailComponent } from './position-detail.component';

describe('NewPositionComponent', () => {
  let component: PositionDetailComponent;
  let fixture: ComponentFixture<PositionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PositionDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PositionDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
