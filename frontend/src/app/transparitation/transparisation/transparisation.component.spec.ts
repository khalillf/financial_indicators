import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransparisationComponent } from './transparisation.component';

describe('TransparisationComponent', () => {
  let component: TransparisationComponent;
  let fixture: ComponentFixture<TransparisationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TransparisationComponent]
    });
    fixture = TestBed.createComponent(TransparisationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
