import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpandedCommunityComponent } from './expanded-community.component';

describe('ExpandedCommunityComponent', () => {
  let component: ExpandedCommunityComponent;
  let fixture: ComponentFixture<ExpandedCommunityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExpandedCommunityComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpandedCommunityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
