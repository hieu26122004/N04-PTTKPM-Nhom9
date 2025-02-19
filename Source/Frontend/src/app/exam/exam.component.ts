import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { Exam } from '../model/Exam';
import { Router } from '@angular/router';
import { ExamService } from '../service/exam.service';

@Component({
  selector: 'app-exam',
  templateUrl: './exam.component.html',
  styleUrls: ['./exam.component.css'],
  standalone: false
})
export class ExamComponent implements OnInit, OnChanges {
  @Input() exam!: Exam; // Đảm bảo exam được truyền vào từ component cha
  @Input() accepted:boolean = true;

  upvotes: number = 0;
  downvotes: number = 0;
  isVoting: boolean = false;

  constructor(private router: Router, private examService: ExamService) { }

  ngOnInit(): void {
    this.upvotes = this.exam.upvoteCount;
    this.downvotes = this.exam.downvoteCount;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['exam'] && this.exam) {
      this.upvotes = this.exam.upvoteCount;
      this.downvotes = this.exam.downvoteCount;
    }
  }

  upvote() {
    this.isVoting = true; // Đặt trạng thái đang xử lý
    this.examService.upvote(this.exam.id).subscribe(
      data => {
        this.upvotes++; // Cập nhật số lượng upvotes ngay lập tức
      },
      error => {
        console.error("Failed to upvote:", error);
      },
      () => {
        this.isVoting = false; // Hoàn tất xử lý
      }
    );
  }
  
  downvote() {
    this.isVoting = true; // Đặt trạng thái đang xử lý
    this.examService.downvote(this.exam.id).subscribe(
      data => {
        this.downvotes++; // Cập nhật số lượng downvotes ngay lập tức
      },
      error => {
        console.error("Failed to downvote:", error);
      },
      () => {
        this.isVoting = false; // Hoàn tất xử lý
      }
    );
  }
  
  save(){
    console.log(this.exam);
  }

  takeExam() {
    this.router.navigate(['/exam', this.exam.id], { state: { exam: this.exam } });
  }

  activeExam(){
    this.examService.activeExam([this.exam]).subscribe();
  }
}
