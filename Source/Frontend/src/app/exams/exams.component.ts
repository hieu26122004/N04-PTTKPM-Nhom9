import { Component, OnInit, OnDestroy, NgZone } from '@angular/core';
import { Exam } from '../model/Exam';
import { ExamService } from '../service/exam.service';

@Component({
  selector: 'app-exams',
  templateUrl: './exams.component.html',
  styleUrls: ['./exams.component.css'],
  standalone: false
})
export class ExamsComponent implements OnInit, OnDestroy {
  exams!: Exam[];
  private eventSource!: EventSource;

  constructor(private examService: ExamService, private zone: NgZone) {}

  ngOnInit(): void {
    const lastExam = new Date().toISOString().replace('Z', '');
    console.log("Last exam date:", lastExam); 

    this.examService.getNextExam('', 10, lastExam).subscribe(
      (exams) => {
        console.log('Loaded exams:', exams);
        this.exams = exams;
      },
      (error) => {
        console.error('Error loading exams:', error);
      }
    );

    this.eventSource = this.examService.establishSseConnection();
    this.eventSource.addEventListener('voteEvent', (event: MessageEvent) => {
      const data = JSON.parse(event.data);
      console.log('Received vote event:', data);
      const exam = this.exams.find((e) => e.examId === data.examId);
      if (exam) {
        this.zone.run(() => { // Đảm bảo thay đổi trong zone của Angular
          if (data.vote === 'UPVOTE') {
            exam.upvoteCount++;
          } else if (data.vote === 'DOWNVOTE') {
            exam.downvoteCount++;
          }
        });
      }
    });
  
    this.eventSource.onerror = (error) => {
      console.error('Error with SSE connection:', error);
      this.eventSource.close();
    };
  }

  ngOnDestroy(): void {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }
}
