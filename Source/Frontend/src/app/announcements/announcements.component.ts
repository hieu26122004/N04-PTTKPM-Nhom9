import { Component, Input, OnInit } from '@angular/core';
import { Announcement } from '../model/class/Announcement';

@Component({
  selector: 'app-announcements',
  templateUrl: './announcements.component.html',
  styleUrls: ['./announcements.component.css'],
  standalone: false
})
export class AnnouncementsComponent implements OnInit {
  @Input() announcements: Announcement[] = [];
  @Input() isTeacher = false;

  constructor() {}

  ngOnInit(): void {
    this.sortAnnouncements();
  }

  getAnnouncementIcon(type: string): string {
    switch (type.toLowerCase()) {
      case 'urgent':
        return 'fa-exclamation-triangle';
      case 'important':
        return 'fa-info-circle';
      case 'info':
      default:
        return 'fa-bell';
    }
  }

  isExpired(announcement: Announcement): boolean {
    return announcement.expiresAt && new Date(announcement.expiresAt) < new Date();
  }

  private sortAnnouncements(): void {
    this.announcements.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  }
}
