import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  isSignIn: boolean;
  isRegister: boolean;
  isForgetPassword: boolean;

  signInForm: FormGroup;

  constructor() { }

  ngOnInit() {
    this.switchToSignIn();
    this.createSignInForm();
  }

  switchToSignIn() {
    this.isSignIn = true;
    this.isRegister = false;
    this.isForgetPassword = false;
  }

  switchToRegister() {
    this.isSignIn = false;
    this.isRegister = true;
    this.isForgetPassword = false;
  }

  switchToForgetPassword() {
    this.isSignIn = false;
    this.isRegister = false;
    this.isForgetPassword = true;
  }

  createSignInForm() {
    this.signInForm = new FormGroup({
      username: new FormControl(''),
      password: new FormControl('')
    });
  }

  signIn() {
    console.log(this.signInForm.value);
  }

}
