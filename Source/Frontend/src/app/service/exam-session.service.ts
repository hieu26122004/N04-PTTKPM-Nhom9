import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ExamSession } from '../model/ExamSession';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ExamSessionService {

  baseUrl = 'http://localhost:8082/exam-session';

  constructor(private http: HttpClient) { }

  saveSession(examSession: ExamSession): Observable<ExamSession> {
    return this.http.post<ExamSession>(`${this.baseUrl}`, examSession);
  }

  getIncompleteSession():Observable<ExamSession[]> {
    return this.http.get<ExamSession[]>(`${this.baseUrl}/incomplete`);
  }

  deleteSession(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

}
