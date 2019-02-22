import {Injectable} from "@angular/core";
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler, HttpHeaders,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from "@angular/common/http";
import {MessageService} from "../service/message.service";
import {Observable} from "rxjs";
import {tap} from "rxjs/operators";
import {SpinnerVisibilityService} from "ng-http-loader";
import {Router} from "@angular/router";

const UNAUTHORIZED = 'Unauthorized';
const E017_ERROR_CODE = 'E017';
const E018_ERROR_CODE = 'E018';
const INVALID_USERNAME_OR_PASSWORD_MESSAGE = 'Błędna nazwa użytkownika lub hasło';

@Injectable()
export class RequestInterceptor implements HttpInterceptor {

  constructor(private messageService: MessageService,
              private spinner: SpinnerVisibilityService,
              private router: Router) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.spinner.show();
    let currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if (currentUser) {
      request = request.clone({
        headers: new HttpHeaders({
          'Authorization': currentUser
        })
      });
    }
    return next.handle(request)
      .pipe(
        tap(event => {
          if (event instanceof HttpResponse) {
            this.spinner.hide();
          }
        }, error => {
          if (error instanceof HttpErrorResponse) {
            this.spinner.hide();
            this.errorIntercept(error.error);
          }
        })
      );
  }

  private errorIntercept(e) { // TODO: Problem with content-type at backend side?
    console.log(e);
    let message = this.getProperMessage(e.message);
    this.logoutIfNecessary(e.code);

    this.messageService.error(message, 5000);
  }

  private getProperMessage(message) {
    if (UNAUTHORIZED === message) {
      return INVALID_USERNAME_OR_PASSWORD_MESSAGE;
    }
    return message;
  }

  private logoutIfNecessary(code) {
    console.log(E017_ERROR_CODE === code || E018_ERROR_CODE === code);
    if (E017_ERROR_CODE === code || E018_ERROR_CODE === code) {
      localStorage.removeItem('currentUser');
      this.router.navigate(['/dashboard']);
    }
  }

}
