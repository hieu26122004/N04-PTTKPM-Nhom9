import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PAGResponse } from '../model/chat/PAGResponse';
import { PrivateChatMessage } from '../model/PrivateChatMessage';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private baseUrl = 'http://localhost:8085/messages';

  constructor(private http: HttpClient) { }

  public getParticipantsAndGroups(lastTimeLoad: string): Observable<PAGResponse> {
    const params = new HttpParams().set('last-time-load', lastTimeLoad);
    return this.http.get<PAGResponse>(`${this.baseUrl}/pag`, { params });
  }

  public getMessages(lastTimeLoad:string,targetUserId:string):Observable<PrivateChatMessage[]>{
    const params = new HttpParams().set('last-time-load', lastTimeLoad).set('target', targetUserId);
    return this.http.get<PrivateChatMessage[]>(`${this.baseUrl}/recent`, { params });
  }
}
