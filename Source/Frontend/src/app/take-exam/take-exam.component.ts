import { Component, OnInit, OnDestroy } from '@angular/core';
import { Question } from '../model/Question';
import { QuestionService } from '../service/question.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Attempt } from '../model/Attempt';
import { Option } from '../model/Option';
import { ExamService } from '../service/exam.service';
import { Exam } from '../model/Exam';

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

  remainingTime: number = 0;
  timer: any;

  constructor(
    private questionService: QuestionService,
    private activatedRoute: ActivatedRoute,
    private examService: ExamService,
    private router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    this.examId = this.getExamIdFromRoute();
    await this.loadExamDetails();
    await this.loadAllQuestions();
    this.initializeTimer();
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  private getExamIdFromRoute(): string {
    return this.activatedRoute.snapshot.paramMap.get('id') || '';
  }

  private async loadExamDetails(): Promise<void> {
    try {
      const examData = history.state.exam;
      if (examData) {
        this.exam = examData;
      } else {
        this.exam = await this.examService.getExamById(this.examId).toPromise();
      }
      this.remainingTime = (this.exam?.duration || 0) * 60;
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

    this.attempt = {
      attemptId: '',
      userId: '',
      examId: this.examId,
      examName: this.exam.name,
      totalTime: 0,
      userAnswer: this.questions.map((question) => ({
        userAnswer: '',
        examId: this.examId,
        questionId: question.questionId,
        isCorrect: false,
      })),
      questions: this.questions,
      result: null,
      isNew: true,
      timestamp: new Date(),
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

  private startTimer(): void {
    this.timer = setInterval(() => {
      if (this.remainingTime > 0) {
        this.remainingTime--;
      } else {
        this.submitExam();
      }
    }, 1000);
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
      this.isSelected[this.index] = true;
      console.log(`Answer for question ${this.getCurrentQuestion().questionOrder}: ${option.content}`);
    }
  }

  async submitExam(): Promise<void> {
    if (!this.attempt) return;

    this.calculateTotalTime();

    try {
      const response = await this.examService.submitExam(this.attempt).toPromise();
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
    this.router.navigate([`/attempt-details`, response.attemptId], {
      state: { attempt: response },
    });
  }

  private handleError(error: any): void {
    console.error('Error submitting exam:', error);
    this.router.navigate(['/error'], { state: { error } });
  }
}
