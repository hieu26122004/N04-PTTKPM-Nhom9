import { Component, OnInit } from '@angular/core';
import { Attempt } from '../model/Attempt';
import { ActivatedRoute } from '@angular/router';
import { AttemptService } from '../service/attemp-service.service';
import { Exam } from '../model/Exam';
import { ExamService } from '../service/exam.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { switchMap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-attempt-details',
  templateUrl: './attempt-details.component.html',
  styleUrls: ['./attempt-details.component.css'],
  standalone: false
})
export class AttemptDetailsComponent implements OnInit {
  attempt!: Attempt;
  attemptId!: string;
  exam!: Exam;

  constructor(
    private activatedRoute: ActivatedRoute,
    private attemptService: AttemptService,
    private examService: ExamService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.activatedRoute.paramMap
      .pipe(
        switchMap(params => {
          this.attemptId = params.get('attemptId')!;
          const attemptData = history.state.attempt;
          if (attemptData) {
            this.attempt = attemptData;
            console.log('Attempt details from state: ', this.attempt);
            return this.loadExam$(this.attempt.examId);
          }
          return this.loadAttempt$();
        }),
        catchError(err => {
          console.error('Error occurred:', err);
          return of(null);
        })
      )
      .subscribe();
  }

  private loadAttempt$() {
    return this.attemptService.getAttemptDetailsById(this.attemptId).pipe(
      switchMap(attempt => {
        this.attempt = attempt;
        console.log('Attempt details from API: ', this.attempt);
        return this.loadExam$(this.attempt.examId);
      })
    );
  }

  private loadExam$(examId: string) {
    if (!examId) {
      console.error('Attempt does not have a valid examId:', this.attempt);
      return of(null);
    }
    return this.examService.getExamById(examId).pipe(
      switchMap(exam => {
        this.exam = exam;
        console.log('Exam details from API: ', this.exam);
        return of(exam);
      }),
      catchError(err => {
        console.error('Error fetching exam details:', err);
        return of(null);
      })
    );
  }

  sanitizeHtml(content: string): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(content);
  }
}
