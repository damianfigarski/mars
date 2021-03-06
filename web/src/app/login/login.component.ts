import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ValidationMessageService} from "../service/validation-message.service";
import {MarsErrorStateMatcher} from "../util/mars-error-state-matcher";
import {AuthenticationService} from "../service/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";

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

  returnUrl: string;

  constructor(public validationMessage: ValidationMessageService,
              private authenticationService: AuthenticationService,
              private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    this.router.navigate([this.returnUrl]);
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
    this.authenticationService.login(this.signInForm.value).subscribe(() => {
      this.router.navigate([this.returnUrl]);
    });
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
