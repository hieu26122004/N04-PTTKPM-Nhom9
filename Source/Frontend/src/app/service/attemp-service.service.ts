import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map, switchMap } from 'rxjs';
import { Attempt } from '../model/Attempt';
import { QuestionService } from './question.service';

@Injectable({
  providedIn: 'root'
})
export class AttemptService {

  constructor(
    private http: HttpClient,
    private questionService: QuestionService
  ) { }

  getAttemptById(id: string) : Observable<Attempt>{
    return this.http.get<Attempt>(`http://localhost:8082/attempts/${id}`);
  }

  getAttemptDetailsById(id: string): Observable<Attempt> {
    const attemptDetails$ = this.http.get<Attempt>(`http://localhost:8082/attempts/details/${id}`);

    return attemptDetails$.pipe(
      switchMap((attemptResponse: Attempt) => {
        return this.questionService.getQuestionsByExamId(attemptResponse.examId).pipe(
          map((questions) => {
            attemptResponse.questions = questions;
            return attemptResponse;
          })
        );
      })
    );
  }

  getAttemptsByUserId(): Observable<Attempt[]> {
    return this.http.get<Attempt[]>(`http://localhost:8082/attempts`);
  }
}
