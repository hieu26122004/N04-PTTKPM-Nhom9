import { Injectable } from '@angular/core';
import { Question } from '../model/Question';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {
  private apiUrl = 'http://localhost:8081/questions';

  constructor(private http: HttpClient) { }

  createQuestion(questions: Question[]): Observable<Question[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post<Question[]>(`${this.apiUrl}`, questions, { headers });
  }

  getQuestionsByExamId(examId: string): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.apiUrl}/exam/${examId}`);
  }
}

