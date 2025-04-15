import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewApplicationComponent } from './new-application.component';

describe('NewApplicationComponent', () => {
  let component: NewApplicationComponent;
  let fixture: ComponentFixture<NewApplicationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewApplicationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewApplicationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
