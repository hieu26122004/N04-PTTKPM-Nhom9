import { Component, OnInit } from '@angular/core';
import { Question } from '../model/Question';
import { ShareRefService } from '../service/share-ref.service';
import { ExamService } from '../service/exam.service';
import { QuestionService } from '../service/question.service';

@Component({
  selector: 'app-create-question',
  templateUrl: './create-question.component.html',
  styleUrls: ['./create-question.component.css'],
  standalone: false
})
export class CreateQuestionComponent implements OnInit {
  questionContent: string = ''; // Nội dung câu hỏi
  optionContents: string[] = ['', '', '', '']; // Các lựa chọn của câu hỏi
  optionIsCorrect: boolean[] = [false, false, false, false]; // Các trạng thái đúng/sai của lựa chọn
  index: number = 0; // Chỉ số của câu hỏi hiện tại
  questionRequests!: Question[]; // Mảng các câu hỏi
  canSubmit: boolean = false; // Biến để điều khiển khả năng submit câu hỏi

  constructor(private shareRefService: ShareRefService , private examService:ExamService, private questionService:QuestionService) {}

  ngOnInit(): void {
    this.questionRequests = this.shareRefService.questions;
    this.updateQuestionView();
  }

  updateQuestionView(): void {
    const currentQuestion = this.questionRequests[this.index];
    this.questionContent = currentQuestion.content;
    this.optionContents = currentQuestion.options.map(option => option.content);
    this.optionIsCorrect = currentQuestion.options.map(option => option.isCorrect);
    this.checkCanSubmit();
  }

  checkCanSubmit(): void {
    const allOptionsFilled = this.optionContents.every(content => content.trim() !== '');
    const isValidQuestion = this.questionContent.trim() !== '' && allOptionsFilled;
    this.canSubmit = isValidQuestion;
  }


  nextQuestion() {
    this.saveCurrentQuestion();
    if (this.index < this.questionRequests.length - 1) {
      this.index++;
      this.updateQuestionView();
    }
  }
  previousQuestion() {
    this.saveCurrentQuestion();
    if (this.index > 0) {
      this.index--;
      this.updateQuestionView();
    }
  }
  saveCurrentQuestion() {
    const currentQuestion = this.questionRequests[this.index];
    currentQuestion.content = this.questionContent;
    currentQuestion.options = this.optionContents.map((content, index) => ({
      label: currentQuestion.options[index].label,
      content: content,
      isCorrect: this.optionIsCorrect[index]
    }));
  }
  selectQuestion(index: number) {
    this.saveCurrentQuestion();
    this.index = index;
    this.updateQuestionView();
  }

  onSubmit() {
    this.saveCurrentQuestion();

    const question: Question = {
      examId: this.shareRefService.exam.id,
      questionId: this.questionRequests[this.index].questionId,
      content: this.questionContent,
      options: this.optionContents.map((content, index) => ({
        label: String.fromCharCode(65 + index),
        content: content,
        isCorrect: this.optionIsCorrect[index]
      })),
      questionOrder: this.index + 1,
      isNew: true
    };
    console.log(this.questionRequests);
    this.examService.createExam(this.shareRefService.exam).subscribe({
      next: (response) => {
        console.log('Exam created successfully:', response);
        this.questionRequests.forEach((question, index) => {
          question.examId = response.id;
        })
        this.questionService.createQuestion(this.questionRequests).subscribe({
          next: (response) => {
            console.log('Questions created successfully:', response);
          },
          error: (err) => {
            console.error('Error creating questions:', err);
          }
        });
      },
      error: (err) => {
        console.error('Error creating exam:', err);
      }
    })
  }
}
