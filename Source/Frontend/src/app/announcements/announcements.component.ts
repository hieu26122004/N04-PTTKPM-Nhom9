import { animate, style, transition, trigger } from '@angular/animations';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-announcements',
  standalone: false,
  templateUrl: './announcements.component.html',
  styleUrl: './announcements.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('200ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class AnnouncementsComponent {
  @Input() announcements: any[] = [];
  @Input() isTeacher = false;
}
