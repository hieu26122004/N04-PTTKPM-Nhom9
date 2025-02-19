import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClassService } from '../service/class.service';
import { Class } from '../model/Class';
import { Subscription, combineLatest, switchMap } from 'rxjs';
import { AuthService } from '../service/auth-service.service';
import { ExamService } from '../service/exam.service';
import { Exam } from '../model/Exam';

@Component({
  selector: 'app-class-details',
  templateUrl: './class-details.component.html',
  styleUrls: ['./class-details.component.css'],
  standalone: false
})
export class ClassDetailsComponent implements OnInit, OnDestroy {
  private subs = new Subscription();
  classId: string;
  clazz: Class;
  inviteLink: string;
  exams: Exam[] = [];
  isTeacher = false;
  selectedTabIndex = 0;

  readonly tabs = [
    { name: 'Participants', icon: 'bi bi-people' },
    { name: 'Announcements', icon: 'bi bi-megaphone' },
    { name: 'Materials', icon: 'bi bi-folder2' },
    { name: 'Assignments', icon: 'bi bi-journal-text' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private classService: ClassService,
    private authService: AuthService,
    private examService: ExamService
  ) {}

  ngOnInit(): void {
    this.subs.add(
      this.route.paramMap.pipe(
        switchMap(params => {
          this.classId = params.get('id');
          return combineLatest([
            this.classService.getClassDetails(this.classId),
            this.authService.isTeacherOfClass(this.classId),
            this.classService.getInviteLink(this.classId)
          ]);
        })
      ).subscribe(
        ([clazz, isTeacher, inviteLink]) => {
          this.clazz = clazz;
          this.isTeacher = isTeacher;
          this.inviteLink = inviteLink;
        }
      )
    );
  }

  onTabChange(index: number): void {
    this.selectedTabIndex = index;
    if (index === 3) this.loadExams();
  }

  loadExams(): void {
    this.subs.add(
      this.examService.getExamByClass(this.classId)
        .subscribe(exams => this.exams = exams)
    );
  }

  takeExam(exam: Exam): void {
    this.router.navigate(['/take', exam.id]);
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
  copyToClipboard(event: MouseEvent, link: string): void {
    event.preventDefault(); // Ngăn chuyển hướng link
    navigator.clipboard.writeText(link).then(() => {
      alert('Invite link copied to clipboard! 🎉');
    }).catch(err => {
      console.error('Failed to copy the invite link: ', err);
    });
  }
}
