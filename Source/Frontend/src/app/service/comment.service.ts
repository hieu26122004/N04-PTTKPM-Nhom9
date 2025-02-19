import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Comment } from '../model/Comment'; // Đảm bảo import đúng đường dẫn

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  baseUrl = 'http://localhost:8081/comment';

  constructor(private http: HttpClient) { }

  getCommentByExamId(
    examId: string,
    parentId?: string,
    lastPosition?: number[] | string | Date,
    lastId?: string
  ): Observable<Comment[]> {
    let params = new HttpParams().set('examId', examId);

    if (parentId) {
      params = params.set('parentId', parentId);
    }

    if (lastPosition) {
      let lastPositionString: string;
      if (Array.isArray(lastPosition)) {
        lastPositionString = this.convertArrayToISODateTime(lastPosition);
      } else if (lastPosition instanceof Date) {
        lastPositionString = lastPosition.toISOString();
      } else {
        lastPositionString = lastPosition;
      }
      params = params.set('lastPosition', lastPositionString);
    }

    if (lastId) {
      params = params.set('lastId', lastId);
    }

    return this.http.get<Comment[]>(`${this.baseUrl}/exam/${examId}`, { params });
  }
  createComment(comment: Comment, examId: string): Observable<Comment> {
    return this.http.post<Comment>(`${this.baseUrl}`, comment);
  }

  addReply(parentId: string, comment: Comment): Observable<Comment> {
    return this.http.post<Comment>(`${this.baseUrl}/reply/${parentId}`, comment);
  }


  convertArrayToISODateTime(dateTimeArray: number[]): string {
    if (!Array.isArray(dateTimeArray) || dateTimeArray.length < 6) {
      throw new Error('Invalid dateTimeArray format. Must have at least 6 elements.');
    }
  
    const [year, month, day, hour, minute, second, nanoSecond = 0] = dateTimeArray;
    const milliSecond = Math.floor(nanoSecond / 1000000);
  
    return `${year.toString().padStart(4, '0')}-` +
           `${month.toString().padStart(2, '0')}-` +
           `${day.toString().padStart(2, '0')}T` +
           `${hour.toString().padStart(2, '0')}:` +
           `${minute.toString().padStart(2, '0')}:` +
           `${second.toString().padStart(2, '0')}.` +
           `${milliSecond.toString().padStart(3, '0')}`;
  }
}
