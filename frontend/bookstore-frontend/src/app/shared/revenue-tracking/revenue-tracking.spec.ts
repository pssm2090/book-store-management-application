import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RevenueTracking } from './revenue-tracking';

describe('RevenueTracking', () => {
  let component: RevenueTracking;
  let fixture: ComponentFixture<RevenueTracking>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RevenueTracking]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RevenueTracking);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
