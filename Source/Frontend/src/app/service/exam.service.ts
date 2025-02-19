import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Exam } from '../model/Exam';
import { ExamRequest } from '../model/ExamRequest';
import { Attempt } from '../model/Attempt';

@Injectable({
  providedIn: 'root'
})
export class ExamService {

  private apiUrl = 'http://localhost:8081/exams';

  constructor(private http: HttpClient) { }

  getNextExam(subject: string = '', limit: number = 10, lastExam: string = ''): Observable<Exam[]> {
    let params = new HttpParams();
    
    if (subject) {
      params = params.set('subject', subject);
    }
    if (limit) {
      params = params.set('limit', limit.toString());
    }
    if (lastExam) {
      params = params.set('lastExam', lastExam);
    }

    return this.http.get<Exam[]>(this.apiUrl, { params });
  }

  getNotAcceptedExams(): Observable<Exam[]> {
    return this.http.get<Exam[]>(`${this.apiUrl}/not-accept`);
  }

  createExam(exam: ExamRequest): Observable<ExamRequest> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    exam.difficultyLevel = exam.difficultyLevel.toUpperCase();
    exam.subject = exam.subject.toUpperCase();
    return this.http.post<ExamRequest>(`${this.apiUrl}/create`, exam, { headers });
  }

  getExamById(id: string): Observable<Exam> {
    return this.http.get<Exam>(`${this.apiUrl}/${id}`);
  }

  submitExam(attempt: Attempt): Observable<Attempt> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post<Attempt>(`http://localhost:8082/exam/submission`, attempt, { headers });
  }

  activeExam(exams: Exam[]): Observable<Exam[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
  
    // Đảm bảo bạn đang gửi một mảng các bài thi, chứ không phải một bài thi duy nhất
    return this.http.put<Exam[]>(`${this.apiUrl}/activate`, exams, { headers });
  }
  

  getExamByClass(classId: string): Observable<Exam[]> {
    return this.http.get<Exam[]>(`${this.apiUrl}/class/${classId}`);
  }

  upvote(examId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${examId}/up`, {});
  }

  downvote(examId: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/${examId}/down`, {});
  }

  establishSseConnection(): EventSource {
    return new EventSource(`${this.apiUrl}/votes/stream`);
  }

  getExamByUser(): Observable<Exam[]> {
    return this.http.get<Exam[]>(`${this.apiUrl}/user`);
  }
}
