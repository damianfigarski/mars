import { Component } from '@angular/core';
import {Spinkit} from "ng-http-loader";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  static API_URL = 'http://192.168.99.100:8080';
  title = 'Mars';
  public spinkit = Spinkit;
}
