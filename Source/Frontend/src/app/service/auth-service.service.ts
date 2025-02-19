import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { jwtDecode } from "jwt-decode";
import { Observable, catchError, map, of } from "rxjs";
import { CustomJwtPayload } from "../../CustomJwtPayload";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    const url = 'http://localhost:8080/auth/login';
    const body = { username, password };
    console.log(body);

    return this.http.post(url, body, { responseType: 'text' });
  }


  getPermissions():Observable<string[]>{
    const url = 'http://localhost:8080/auth/permission';
    return this.http.get<string[]>(url);
  }

  getUserIdByUsername(username: string):Observable<string>{
    const params = new HttpParams().set('username', username);
    const url = 'http://localhost:8080/auth/userId';
    return this.http.get<string>(url, 
      { params:  params , 
        responseType: 'text' as 'json' });
  }

  isTeacherOfClass(clazzId: string): Observable<boolean> {
    return this.getPermissions().pipe(
      map((permissions) => {
        console.log(permissions);
        return permissions.includes(clazzId + '.TEACHER');
      }),
      catchError((error) => {
        console.error('Error getting permissions:', error);
        return of(false); // Trả về `false` nếu gặp lỗi
      })
    );
  }
  
}
