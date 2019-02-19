import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {map} from "rxjs/operators";
import {AppComponent} from "../app.component";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(private http: HttpClient) {
  }

  login(value: any) {
    return this.http.post(AppComponent.API_URL + '/login', value,
      {responseType: 'text', observe: 'response'})
      .pipe(map((response: HttpResponse<string>) => {
        // login successful if there's a jwt token in the response
        let user = response.headers.get('Authorization');
        if (user) {
          // store user details and jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify(user));
        }
      }));
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
  }

}
