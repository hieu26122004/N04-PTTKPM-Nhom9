import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ClassDto } from '../model/ClassDto';
import { Observable } from 'rxjs';
import { Class } from '../model/Class';

@Injectable({
  providedIn: 'root'
})
export class ClassService {

  private url = "http://localhost:8088/class";

  constructor(
    private http: HttpClient
  ) { }

  
  createClass(classDto: ClassDto): Observable<Class> {
    return this.http.post<Class>(`${this.url}/create`, classDto);
  }

  getAllClasses():Observable<Class[]>{
    return this.http.get<Class[]>(this.url);
  }

  getClassDetails(id: string): Observable<Class> {
    return this.http.get<Class>(`${this.url}/${id}`);
  }
  getInviteLink(id: string): Observable<string> {
    const params = new HttpParams().set('class-id', id);
    return this.http.get<string>(`${this.url}/invite-link`, { 
      params: params, 
      responseType: 'text' as 'json'
    });
  }
  
  joinClass(inviteLink: string, studentName: string): Observable<String> {
    // Mã hóa studentName để tránh lỗi với các ký tự đặc biệt trong URL
    const encodedStudentName = encodeURIComponent(studentName);
    const joinClassUrl  = inviteLink + '&username=' + encodedStudentName;
    
    console.log("Join class URL: ", joinClassUrl);
    
    return this.http.get<String>(joinClassUrl, { responseType: 'text' as 'json' });
  }
  
  
}
