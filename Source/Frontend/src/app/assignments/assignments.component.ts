import { Component, EventEmitter, Input, OnInit, OnChanges, SimpleChanges, Output } from '@angular/core';
import { Exam } from '../model/Exam';

@Component({
  selector: 'app-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css'],
  standalone: false
})
export class AssignmentsComponent {
  @Input() exams: Exam[] = [];
  @Output() takeExam = new EventEmitter<Exam>();

  onTakeExam(exam: Exam): void {
    this.takeExam.emit(exam);
  }
}
