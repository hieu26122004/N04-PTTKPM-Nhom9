import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttemptComponent } from './attempt-details.component';

describe('AttempComponent', () => {
  let component: AttemptComponent;
  let fixture: ComponentFixture<AttemptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AttemptComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AttemptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
