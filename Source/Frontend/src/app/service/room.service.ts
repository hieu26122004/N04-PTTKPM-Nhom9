import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  baseUrl = 'http://localhost:8085';

  constructor(private http: HttpClient) { }

  getRooms(subject:string):Observable<string[]> {
    return this.http.get<any[]>(`${this.baseUrl}/rooms/${subject}`, {
    });
  }

  createRoom(subject:string):Observable<string> {
    return this.http.post<any>(`${this.baseUrl}/rooms/${subject}`, {
      responseType: 'text' as 'json'
    });
  }

}
