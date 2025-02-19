import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth-service.service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [AuthService],
  standalone: false
})
export class LoginComponent implements OnInit {
  errorMessage: string = '';
  formLogin!: FormGroup;
  wrongUsername: boolean = false;
  wrongPassword: boolean = false;
  loginRequest:any;

  constructor(private authService: AuthService, private router: Router) { }
  ngOnInit(): void {
    this.formLogin = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });

    this.loginRequest = {
      username: '',
      password: ''
    };
  }

  login(){
    console.log(this.formLogin.value);

    this.loginRequest.password = this.formLogin.value.password;
    this.loginRequest.username = this.formLogin.value.username;

    this.authService.login(this.loginRequest.username,this.loginRequest.password)
    .subscribe(
      response=>{
        console.log(response);
        localStorage.setItem('token', response);
        this.router.navigate(['/home']);
      },
      error=>{
        if(error.status == 401){
          this.wrongUsername = true;
          this.wrongPassword = true;
          this.errorMessage = 'Username or Password is incorrect!';
        }
        console.log(error);
      }
    );
  }
}
