import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookControls } from './book-controls';

describe('BookControls', () => {
  let component: BookControls;
  let fixture: ComponentFixture<BookControls>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookControls]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookControls);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
