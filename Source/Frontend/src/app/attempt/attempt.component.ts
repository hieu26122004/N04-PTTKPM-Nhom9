import { Component, Input } from '@angular/core';
import { Attempt } from '../model/Attempt';
import { Router } from '@angular/router';

@Component({
  selector: 'app-attempt',
  templateUrl: './attempt.component.html',
  styleUrl: './attempt.component.css',
  standalone: false
})
export class AttemptComponent {
  @Input() attempt!: Attempt;

  constructor(private router:Router){}

  attemptDetails(attempt: Attempt) {
    this.router.navigate([`/attempt-details`, attempt.attemptId]);
  }
}
