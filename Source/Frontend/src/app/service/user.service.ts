import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Profile } from '../model/Profile';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl = 'http://localhost:8083';

  constructor(private http:HttpClient) { }

  getUserProfile(userId:string):Observable<Profile> {
    if(userId == null) {
      return this.http.get<Profile>(`${this.baseUrl}/profile`);
    }
    return this.http.get<Profile>(`${this.baseUrl}/profile/${userId}`);
  }
}
