import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PositionViewComponent } from './position-view.component';

describe('PositionViewComponent', () => {
  let component: PositionViewComponent;
  let fixture: ComponentFixture<PositionViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PositionViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PositionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
