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

@Injectable()
export class RequestInterceptor implements HttpInterceptor {

  constructor(private messageService: MessageService,
              private spinner: SpinnerVisibilityService) {
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
            this.errorIntercept(error);
          }
        })
      );
  }

  private errorIntercept(e) { // TODO: Logout when error is E017 or E018
    if (e.status === 401) {
      this.messageService.error('Błędna nazwa użytkownika lub hasło', 5000);
    } else {
      this.messageService.error(e.error.message, 5000);
    }
  }

}
