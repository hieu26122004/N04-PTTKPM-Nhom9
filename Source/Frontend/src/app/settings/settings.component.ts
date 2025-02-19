import { Component, OnInit } from '@angular/core';
import { UserService } from '../service/user.service';
import { Profile } from '../model/Profile';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css'],
  standalone: false
})
export class SettingsComponent implements OnInit {
  profile: Profile = {} as Profile;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getUserProfile(null).subscribe((data: Profile) => {
      this.profile = data;
      console.log(this.profile);
    });
  }
}