import { Injectable } from '@angular/core';
import { Exam } from '../model/Exam';
import { Question } from '../model/Question';
import { ExamRequest } from '../model/ExamRequest';
import { Class } from '../model/Class';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ShareRefService {

  exam!:ExamRequest;
  questions!:Question[];
  navHeight!:number;

  private clazzSubject = new BehaviorSubject<Class[]>([]);
  clazz$ = this.clazzSubject.asObservable();
  constructor() { }
  
  updateClazz(clazz: Class[]) {
    this.clazzSubject.next(clazz);
  }
}
