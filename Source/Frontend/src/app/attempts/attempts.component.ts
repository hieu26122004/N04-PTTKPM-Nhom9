import { Component, OnInit } from '@angular/core';
import { AttemptService } from '../service/attemp-service.service';
import { Attempt } from '../model/Attempt';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-attempts',
  templateUrl: './attempts.component.html',
  styleUrl: './attempts.component.css',
  standalone: false
})
export class AttemptsComponent implements OnInit {

  attempts: Attempt[] = [];
  errorMessage: string | null = null;

  constructor(private attemptService: AttemptService) {}

  ngOnInit(): void {
    this.loadAttempts();
  }

  loadAttempts(): void {
    this.attemptService.getAttemptsByUserId()
      .pipe(
        catchError((error) => {
          console.error('Error fetching attempts:', error);
          this.errorMessage = 'Failed to load attempts. Please try again later.';
          return of([]); // Trả về danh sách rỗng để tránh lỗi
        })
      )
      .subscribe((response) => {
        if (response) {
          this.attempts = response;
          console.log('Attempts by userId from API: ', this.attempts);
        } else {
          console.warn('No attempts found for the current user.');
          this.errorMessage = 'No attempts found for the current user.';
        }
      });
  }
}