import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ShareRefService } from './service/share-ref.service';
import { ExamSessionService } from './service/exam-session.service';
import { ExamSession } from './model/ExamSession';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
    trigger('toastAnimation', [
      transition(':enter', [
        style({ transform: 'translateX(100%)', opacity: 0 }),
        animate('300ms ease-out', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ transform: 'translateX(100%)', opacity: 0 }))
      ])
    ])
  ],
  standalone: false
})
export class AppComponent implements AfterViewInit, OnInit, OnDestroy {
  title = 'pratice-make-perfect';
  incompleteExam: ExamSession[] = [];
  toastTimeouts: number[] = [];

  @ViewChild('navComponent', { static: false }) navComponent!: ElementRef;

  constructor(
    private shareRefService: ShareRefService, 
    private examSessionService: ExamSessionService
  ) {}

  ngOnDestroy(): void {
    this.toastTimeouts.forEach(timeout => clearTimeout(timeout));
  }

  ngOnInit(): void {
    this.loadIncompleteExams();
  }

  ngAfterViewInit(): void {
    const navHeight = this.navComponent.nativeElement.offsetHeight;
    console.log('Chiều cao của app-nav:', navHeight);
    this.shareRefService.navHeight = navHeight;
  }

  private loadIncompleteExams(): void {
    this.examSessionService.getIncompleteSession().subscribe({
      next: (data) => {
        this.incompleteExam = data;
        this.showToasts();
      },
      error: (error) => {
        console.error('Lỗi khi tải bài thi chưa hoàn thành:', error);
      }
    });
  }

  private showToasts(): void {
    this.incompleteExam.forEach((exam, index) => {
      const timeout = window.setTimeout(() => {
        this.removeToast(index);
      }, 100000 + (index * 1000)); // Mỗi toast sẽ hiển thị 5 giây và biến mất lần lượt
      
      this.toastTimeouts.push(timeout);
    });
  }

  removeToast(index: number): void {
    if (this.incompleteExam[index]) {
      this.incompleteExam = this.incompleteExam.filter((_, i) => i !== index);
    }
  }

  trackBySessionId(index: number, item: ExamSession): string {
    return item.sessionId;
  }

  get navHeight(): number {
    return this.shareRefService.navHeight;
  }
}