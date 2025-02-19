import { Component, OnInit } from '@angular/core';
import { Exam } from '../model/Exam';
import { UserService } from '../service/user.service';
import { ExamService } from '../service/exam.service';
import { Profile } from '../model/Profile';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent implements OnInit {
  exams: Exam[];
  savedExams: Exam[];
  profile: Profile;
  createdAt = '';
  timeSinceJoined = '';

  constructor(
    private userService: UserService,
    private examService: ExamService
  ) {}

  ngOnInit(): void {
    this.examService.getExamByUser().subscribe((data) => {
      this.exams = data;
    });

    this.userService.getUserProfile(null).subscribe((data) => {
      this.profile = data;
      // Chuyển đổi mảng ngày thành chuỗi định dạng
      this.createdAt = this.formatDate(
        this.profile.createdAt as unknown as number[]
      );
      console.log('Created At : ' + this.createdAt);

      // Tính thời gian từ khi tham gia
      this.timeSinceJoined = this.calculateTimeSinceJoined(
        this.profile.lastLogin as unknown as number[]
      );
      console.log('Time Since Joined: ' + this.timeSinceJoined);
    });
  }

  private formatDate(dateArray: number[]): string {
    const [year, month, day] = dateArray;
    const months = [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December',
    ];

    const suffix = (n: number) => {
      if (n > 3 && n < 21) return 'th';
      switch (n % 10) {
        case 1:
          return 'st';
        case 2:
          return 'nd';
        case 3:
          return 'rd';
        default:
          return 'th';
      }
    };

    return `${months[month - 1]} ${day}${suffix(day)}, ${year}`;
  }

  private calculateTimeSinceJoined(dateArray: number[]): string {
    const [year, month, day] = dateArray;
    const createdDate = new Date(year, month - 1, day);
    const now = new Date();

    const diffInMilliseconds = now.getTime() - createdDate.getTime();
    const seconds = Math.floor(diffInMilliseconds / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) {
      return `${days} day${days > 1 ? 's' : ''} ago`;
    } else if (hours > 0) {
      return `${hours} hour${hours > 1 ? 's' : ''} ago`;
    } else if (minutes > 0) {
      return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
    } else {
      return `${seconds} second${seconds > 1 ? 's' : ''} ago`;
    }
  }
}

