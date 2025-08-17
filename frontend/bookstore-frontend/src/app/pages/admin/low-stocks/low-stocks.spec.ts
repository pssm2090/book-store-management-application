import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LowStocks } from './low-stocks';

describe('LowStocks', () => {
  let component: LowStocks;
  let fixture: ComponentFixture<LowStocks>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LowStocks]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LowStocks);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
