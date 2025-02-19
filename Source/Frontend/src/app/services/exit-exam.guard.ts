import { Injectable } from '@angular/core';
import { CanDeactivate } from '@angular/router';
import { Observable } from 'rxjs';
import { TakeExamComponent } from '../take-exam/take-exam.component';

@Injectable({
  providedIn: 'root'
})
export class ExitExamGuard implements CanDeactivate<TakeExamComponent> {
  canDeactivate(
    component: TakeExamComponent,
  ): boolean | Observable<boolean> {
    if (!component.isSubmitted) {
      return confirm('Bạn chưa nộp bài thi, bạn có chắc chắn muốn thoát?');
    }
    return true;
  }
}