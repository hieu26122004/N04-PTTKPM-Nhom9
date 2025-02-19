import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {RoomService} from '../service/room.service';
import {log} from '@angular-devkit/build-angular/src/builders/ssr-dev-server';

@Component({
  selector: 'app-expanded-community',
  standalone: false,

  templateUrl: './expanded-community.component.html',
  styleUrl: './expanded-community.component.css'
})
export class ExpandedCommunityComponent implements OnInit {

  subject:string;
  rooms:string[];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private roomService: RoomService) {
  }

  ngOnInit() {
    this.subject = this.activatedRoute.snapshot.params['id'];
    this.roomService.getRooms(this.subject).subscribe(
      {
        next: (value) => {
          this.rooms = value;
        },
        error: (error) => {
          console.log(error);
        }
      }
    )
  }

  createRoom() {
    this.roomService.createRoom(this.subject).subscribe(
      {
        next: (value) => {console.log(value);},
        error: (error) => {console.log(error);},
      }
    );
  }

}
