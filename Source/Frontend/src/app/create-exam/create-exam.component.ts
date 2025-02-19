import { Component, OnInit } from '@angular/core';
import { ExamRequest } from '../model/ExamRequest';
import { ExamService } from '../service/exam.service';
import { ShareRefService } from '../service/share-ref.service';
import { faQuestion } from '@fortawesome/free-solid-svg-icons';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth-service.service';
import { ClassService } from '../service/class.service';
import { Class } from '../model/Class';

@Component({
  selector: 'app-create-exam',
  templateUrl: './create-exam.component.html',
  styleUrls: ['./create-exam.component.css'],
  standalone: false
})
export class CreateExamComponent implements OnInit {
  clazzes: Class[] = [];
  exam: ExamRequest = {  
    id: '',
    name: '',
    subject: '',
    duration: 0,
    provider: '',
    numberOfQuestion: 0,
    difficultyLevel: '',
    due: '',
    maxAttempts: 0,
    classId: ''
  };

  selectSize = 1;

  constructor(private examService: ExamService, 
    private shareRefService: ShareRefService, 
    private router: Router, 
    private authService:AuthService,
    private classService:ClassService) { }
    

    ngOnInit(): void {
      // Gọi API lấy permissions và xử lý
      this.authService.getPermissions().subscribe(
        (response) => {
          console.log('Permissions:', response);
          
          const permissions = response;
          if (permissions && permissions.length > 0) {
            // Lấy danh sách classIds từ permissions
            const clazzIds = permissions.map(item => {
              const parts = item.split('.');
              return parts[0];  // Lấy phần ID trước dấu chấm
            });
  
            // Lấy danh sách lớp từ service và lọc theo các classIds đã có
            this.classService.getAllClasses().subscribe({
              next: (classesResponse) => {
                this.clazzes = classesResponse.filter(clazz => clazz.classId && clazzIds.includes(clazz.classId));
                console.log('Filtered Classes:', this.clazzes);
              },
              error: (err) => {
                console.error('Error getting classes:', err);
              }
            });
  
          } else {
            console.error('No permissions found');
          }
        },
        (error) => {
          console.error('Error getting permissions:', error);
        }
      );
    }

  onFocus() {
    this.selectSize = 10;
  }
  onBlur() {
    this.selectSize = 1;
  }
  onSubmit() {
    console.log('Form data:', this.exam);
    const numberOfQuestions = this.exam.numberOfQuestion;
    this.shareRefService.questions = Array(numberOfQuestions).fill(null).map((_, index) => {
      return {
        examId: this.exam.id,
        questionId: ``,
        content: '',
        options: [
          { label: 'A', content: '', isCorrect: false },
          { label: 'B', content: '', isCorrect: false },
          { label: 'C', content: '', isCorrect: false },
          { label: 'D', content: '', isCorrect: false }
        ],
        questionOrder: index + 1,
        isNew: true
      };
    });

    // Gán exam vào shareRefService
    this.shareRefService.exam = this.exam;

    // Kiểm tra mảng các câu hỏi đã được tạo
    console.log('Questions after assignment:', this.shareRefService.questions);
    this.router.navigate(['/create-question']);
  }

}

    // this.examService.createExam(this.exam).subscribe({
    //   next: (response) => {
    //     console.log('Exam created successfully:', response);
    //   },
    //   error: (err) => {
    //     console.error('Error creating exam:', err);
    //   }
    // });
