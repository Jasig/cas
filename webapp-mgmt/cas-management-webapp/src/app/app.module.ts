import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {AppComponent} from './app.component';
import {Messages} from "./messages";
import {AppRoutingModule} from "./app-routing.module";
import {ServicesModule} from "./services/services.module";
import {HeaderComponent} from "./header/header.component";
import {SharedModule} from "./shared/shared.module";
import {FormModule} from "./form/form.module";


@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    ServicesModule,
    FormModule,
    SharedModule,
    AppRoutingModule
  ],
  declarations: [
    AppComponent,
    HeaderComponent,
  ],
  providers: [
    Messages
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
