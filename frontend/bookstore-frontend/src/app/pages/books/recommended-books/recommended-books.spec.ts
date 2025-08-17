import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecommendedBooks } from './recommended-books';

describe('RecommendedBooks', () => {
  let component: RecommendedBooks;
  let fixture: ComponentFixture<RecommendedBooks>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecommendedBooks]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecommendedBooks);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
