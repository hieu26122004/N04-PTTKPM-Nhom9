import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Question } from '../model/Question';
import { QuestionService } from '../service/question.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Attempt } from '../model/Attempt';
import { Option } from '../model/Option';
import { ExamService } from '../service/exam.service';
import { Exam } from '../model/Exam';
import { ExamSession } from '../model/ExamSession';
import { ExamSessionService } from '../service/exam-session.service';

@Component({
  selector: 'app-take-exam',
  templateUrl: './take-exam.component.html',
  styleUrls: ['./take-exam.component.css'],
  standalone: false
})
export class TakeExamComponent implements OnInit, OnDestroy {
  questions: Question[] = [];
  examId: string = '';
  index: number = 0;
  attempt: Attempt | null = null;
  isSelected: boolean[] = [];
  exam: Exam | null = null;
  startTime: Date;

  remainingTime: number = 0;
  timer: any;
  intervalTimer: any;

  examSession:ExamSession;

  isSubmitted: boolean = false;

  @HostListener('window:beforeunload', ['$event'])
  onBeforeUnload(event: BeforeUnloadEvent): void {
    if (!this.isSubmitted) {
      event.preventDefault();
      event.returnValue = '';
    }
  }

  constructor(
    private questionService: QuestionService,
    private activatedRoute: ActivatedRoute,
    private examService: ExamService,
    private examSessionService: ExamSessionService,
    private router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    this.startTime = new Date();
    this.examId = this.getExamIdFromRoute();
    this.examSession = this.getExamSessionFromRoute();
    await this.loadExamDetails();
    await this.loadAllQuestions();
    this.initializeExamSession();
    this.initializeTimer();
    this.startAutoSaveSession(); 
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  private getExamIdFromRoute(): string {
    return this.activatedRoute.snapshot.paramMap.get('id') || '';
  }
  private getExamSessionFromRoute(): ExamSession {
    const state = window.history.state;
    if (state && state.session) {
      return state.session;
    }
    return null;
  }

  private async loadExamDetails(): Promise<void> {
    try {
      const examData = history.state.exam;
      if (examData) {
        this.exam = examData;
      } else {
        this.exam = await this.examService.getExamById(this.examId).toPromise();
      }
      if (this.examSession && this.examSession.remainTime) {
        this.remainingTime = this.examSession.remainTime;
      } else {
        this.remainingTime = (this.exam?.duration || 0) * 60;
      }
  
    } catch (err) {
      console.error('Error fetching exam details:', err);
    }
  }
  

  private async loadAllQuestions(): Promise<void> {
    try {
      this.questions = await this.questionService.getQuestionsByExamId(this.examId).toPromise();
      this.initializeAttempt();
    } catch (err) {
      console.error('Error loading questions:', err);
    }
  }

  private initializeAttempt(): void {
    if (!this.exam) return;
    const userAnswers = this.examSession && this.examSession.userAnswers
      ? this.examSession.userAnswers
      : this.questions.map((question) => ({
          userAnswer: '',
          examId: this.examId,
          questionId: question.questionId,
          isCorrect: false,
        }));
  
    this.attempt = {
      attemptId: '',
      userId: '',
      examId: this.examId,
      examName: this.exam.name,
      totalTime: 0,
      userAnswer: userAnswers,
      questions: this.questions,
      result: null,
      isNew: true,
      timestamp: this.startTime,
    };
  
    this.isSelected = new Array(this.questions.length).fill(false);
  }
  

  private initializeTimer(): void {
    if (this.remainingTime > 0) {
      this.startTimer();
    } else {
      this.submitExam();
    }
  }

  private initializeExamSession(): void {
    if (!this.examSession) {
      const startTime = new Date();
      const dueTime = this.exam?.due ? new Date(this.exam.due).getTime() : 0;
      const expirationSeconds = dueTime > 0 ? Math.max(0, Math.floor((dueTime - startTime.getTime()) / 1000)) : 0;
  
      this.examSession = {
        examId: this.examId,
        userId: '',
        sessionId: '',
        examName: this.exam?.name || '',
        userAnswers: [],
        startTime: startTime,
        totalTime: 0,
        remainTime: this.remainingTime,
        expirationTime: expirationSeconds,
        expired: false,
      };
  
      console.log(`Expiration Time (seconds): ${this.examSession.expirationTime}`);
    }
  }
  

  private startTimer(): void {
    this.timer = setInterval(() => {
      if (this.remainingTime > 0) {
        this.remainingTime--;
      } else {
        this.submitExam();
      }
    }, 1000);
  }

  private startAutoSaveSession(): void {
    this.intervalTimer = setInterval(() => {
      this.examSession.remainTime = this.remainingTime;
      this.saveSession();
    }, 30000);
  }

  formatTime(seconds: number): string {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const remainingSeconds = seconds % 60;
    return `${this.pad(hours)}:${this.pad(minutes)}:${this.pad(remainingSeconds)}`;
  }

  private pad(num: number): string {
    return num < 10 ? '0' + num : num.toString();
  }

  nextQuestion(): void {
    if (this.index < this.questions.length - 1) {
      this.index++;
    }
  }

  prevQuestion(): void {
    if (this.index > 0) {
      this.index--;
    }
  }

  goToQuestion(index: number): void {
    this.index = index;
  }

  getCurrentQuestion(): Question {
    return this.questions[this.index];
  }

  selectAnswer(option: Option): void {
    if (this.attempt) {
      this.attempt.userAnswer[this.index].userAnswer = option.content;
      this.examSession.userAnswers = this.attempt.userAnswer;
      this.isSelected[this.index] = true;
      console.log(`Answer for question ${this.getCurrentQuestion().questionOrder}: ${option.content}`);
    }
  }

  saveSession(): void {
    if (!this.examSession) {
      console.error('Exam session is not initialized.');
      return;
    }
    this.examSessionService.saveSession(this.examSession).subscribe({
      next: (response) => {
        this.examSession = response; 
        console.log('Exam session saved successfully:', this.examSession);
      },
      error: (error) => {
        console.error('Error saving exam session:', error);
      }
    });
  }
  

  async submitExam(): Promise<void> {
    if (!this.attempt) return;

    this.calculateTotalTime();

    try {
      const response = await this.examService.submitExam(this.attempt).toPromise();
      this.isSubmitted = true;
      this.handleSuccess(response);
    } catch (error) {
      this.handleError(error);
    }
  }

  private calculateTotalTime(): void {
    if (this.exam) {
      this.attempt!.totalTime = (this.exam.duration * 60 - this.remainingTime) / 60;
    }
  }

  private handleSuccess(response: Attempt): void {
    console.log('Exam submitted successfully:', response);
    this.isSubmitted = true;
    this.examSessionService.deleteSession(this.examSession.sessionId).subscribe(
      () => {
        this.router.navigate([`/attempt-details`, response.attemptId], {
          state: { attempt: response },
        });
      },
      (error) => {
        console.error('Error deleting session:', error);
      }
    );
  }
  

  private handleError(error: any): void {
    console.error('Error submitting exam:', error);
    this.router.navigate(['/error'], { state: { error } });
  }
}
