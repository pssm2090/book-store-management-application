import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerBehaviour } from './customer-behaviour';

describe('CustomerBehaviour', () => {
  let component: CustomerBehaviour;
  let fixture: ComponentFixture<CustomerBehaviour>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerBehaviour]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerBehaviour);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
