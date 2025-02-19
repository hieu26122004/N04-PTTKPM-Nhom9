import { Component, OnInit } from '@angular/core';
import { Form, FormControl, FormGroup, Validators } from '@angular/forms';
import { Class } from '../model/Class';
import { ClassService } from '../service/class.service';
import { Router } from '@angular/router';
import { ShareRefService } from '../service/share-ref.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
  standalone: false
})
export class SidebarComponent implements OnInit {

  createClassForm: FormGroup = new FormGroup({
    className: new FormControl('', Validators.required),
    classDescription: new FormControl('', Validators.required),
    teacherName: new FormControl('', Validators.required)
  });

  joinClassForm: FormGroup = new FormGroup({
    inviteLink: new FormControl('', Validators.required),
    studentName: new FormControl('', Validators.required)
  });

  clazz!:Class[];

  constructor(
    private classService: ClassService,
    private router: Router,
    private shareRefService: ShareRefService
  ) { }
  ngOnInit(): void {
    this.classService.getAllClasses().subscribe(
      (response)=>{
        this.clazz = response;
        this.shareRefService.updateClazz(this.clazz);
      },
      (error)=>{
        console.log(error);
      }
    );
  }


  createClass(){
    const classDto = this.createClassForm.value;
    classDto.description = classDto.classDescription;
    this.classService.createClass(classDto).subscribe(
      (response)=>{
        console.log(response);
      },
      (error)=>{
        console.log(error);
      }
    );
  }

  goToClass(clazz:Class){
    this.router.navigate(['/class', clazz.classId]);
  }

  joinClass() {
    const inviteLink = this.joinClassForm.value.inviteLink;
    const studentName = this.joinClassForm.value.studentName;

    console.log('Joining class with invite link:', inviteLink, 'Student name:', studentName);

    // Gọi API để tham gia lớp
    this.classService.joinClass(inviteLink, studentName).subscribe(
      (response) => {
        console.log('Join class response:', response);
        const parts = response.split('/');

      // Phần cuối cùng của mảng sẽ là ID
      const id = parts[parts.length - 1];
        this.router.navigate(['/class', id]);
      },
      (error) => {
        console.log('Error:', error);
        // Xử lý lỗi ở đây (ví dụ: thông báo lỗi cho người dùng)
      }
    );
  }


}
