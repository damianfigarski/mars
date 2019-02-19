import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {EmptyLayoutComponent} from './layouts/empty-layout/empty-layout.component';
import {TemplateLayoutComponent} from './layouts/template-layout/template-layout.component';
import {SideNavComponent} from './side-nav/side-nav.component';
import {AppMaterialModule} from './app-material/app-material.module';
import {HeaderComponent} from './header/header.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {LoginComponent} from './login/login.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AngularFontAwesomeModule} from 'angular-font-awesome';
import {FlexLayoutModule} from "@angular/flex-layout";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {NgHttpLoaderModule} from "ng-http-loader";
import {AuthGuard} from "./guard/auth.guard";
import {RequestInterceptor} from "./interceptor/request-interceptor";

@NgModule({
  declarations: [
    AppComponent,
    EmptyLayoutComponent,
    TemplateLayoutComponent,
    SideNavComponent,
    HeaderComponent,
    DashboardComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    AppMaterialModule,
    AngularFontAwesomeModule,
    FormsModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    HttpClientModule,
    NgHttpLoaderModule.forRoot()
  ],
  providers: [
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: RequestInterceptor,
      multi: true,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
