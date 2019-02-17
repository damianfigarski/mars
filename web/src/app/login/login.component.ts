import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ValidationMessageService} from "../service/validation-message.service";
import {MarsErrorStateMatcher} from "../util/mars-error-state-matcher";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  readonly MIN_PASSWD_LENGTH = 8;

  isSignIn: boolean;
  isRegister: boolean;
  isForgetPassword: boolean;

  signInForm: FormGroup;
  registerForm: FormGroup;
  forgetPasswordForm: FormGroup;

  matcher: MarsErrorStateMatcher;

  constructor(public validationMessage: ValidationMessageService) { }

  ngOnInit() {
    this.switchToSignIn();
    this.createSignInForm();
    this.createRegisterForm();
    this.createForgetPasswordForm();
    this.matcher = new MarsErrorStateMatcher();
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

  createRegisterForm() {
    this.registerForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(this.MIN_PASSWD_LENGTH)]),
      confirmPassword: new FormControl('', [Validators.required, Validators.minLength(this.MIN_PASSWD_LENGTH)])
    });
  }

  registerUser() {
    console.log(this.registerForm.value);
  }

  createForgetPasswordForm() {
    this.forgetPasswordForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email])
    })
  }

  forgetPassword() {
    console.log(this.forgetPasswordForm.value);
  }

}
