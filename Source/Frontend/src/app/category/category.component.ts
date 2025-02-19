import { Component, OnInit } from '@angular/core';
import { Exam } from '../model/Exam';
import { ExamService } from '../service/exam.service';
import { TimeService } from '../time.service';

@Component({
  selector: 'app-category',
  standalone: false,
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css'],
})
export class CategoryComponent implements OnInit {
  isAdmin = false;
  exams: Exam[] = [];
  filteredExams: Exam[] = [];
  examMap: Map<string, Exam[]> = new Map();
  selectedSubject: string = '';
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private examService: ExamService,
    private timeService: TimeService
  ) {}

  ngOnInit(): void {
    this.loadInitialExams();
  }

  loadInitialExams(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.examService.getNextExam().subscribe({
      next: (exams) => {
        const parsedExams = this.parseExams(exams);
        this.exams = parsedExams;
        this.loadExams(parsedExams);
        this.filterExams();
      },
      error: (err) => {
        console.error('Error loading exams:', err);
        this.errorMessage = 'Failed to load exams. Please try again later.';
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }

  parseExams(exams: Exam[]): Exam[] {
    return exams.map((exam) => ({
      ...exam,
      createdDate: this.timeService.parseLocalDateTime(exam.createdDate),
      lastUpdatedDate: this.timeService.parseLocalDateTime(exam.lastUpdatedDate),
    }));
  }

  loadExams(exams: Exam[]): void {
    exams.forEach((exam) => {
      const subject = exam.status === 'NOT_ACCEPTED' ? 'NOT_ACCEPTED' : exam.subject;
      if (!this.examMap.has(subject)) {
        this.examMap.set(subject, []);
      }
      this.examMap.get(subject)?.push(exam);
    });
  }

  filterExams(): void {
    this.filteredExams = this.selectedSubject
      ? this.examMap.get(this.selectedSubject) || []
      : [...this.exams];
  }

  toggleSubject(subject: string): void {
    this.selectedSubject = this.selectedSubject === subject ? '' : subject;
    this.filterExams();
  }

  moreExams(): void {
    if (this.isLoading) return;

    const lastExam = this.exams[this.exams.length - 1];
    if (lastExam?.lastUpdatedDate) {
      this.isLoading = true;
      this.examService.getNextExam('', 10, lastExam.lastUpdatedDate).subscribe({
        next: (exams) => {
          const parsedExams = this.parseExams(exams);
          this.exams.push(...parsedExams);
          this.loadExams(parsedExams);
          this.filterExams();
        },
        error: (err) => {
          console.error('Error loading more exams:', err);
          this.errorMessage = 'Failed to load more exams. Please try again later.';
        },
        complete: () => {
          this.isLoading = false;
        },
      });
    } else {
      console.warn('No valid lastUpdated data found.');
      this.errorMessage = 'Cannot load more exams at the moment.';
    }
  }
}