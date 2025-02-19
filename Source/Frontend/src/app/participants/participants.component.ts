import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-participants',
  standalone: false,
  templateUrl: './participants.component.html',
  styleUrl: './participants.component.css'
})
export class ParticipantsComponent {
  @Input() participants: any[] = [];
  @Input() isTeacher = false;

  @Output() participantRemoved = new EventEmitter<any>();


  removeParticipant(participant: any): void {
    this.participantRemoved.emit(participant);
  }
}
