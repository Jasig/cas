/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from "@angular/forms";
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ReqhandlersComponent } from './reqhandlers.component';
import {Messages} from "../../messages";
import {ServiceData} from "../../../domain/service-edit-bean";
import {SharedModule} from "../../shared/shared.module";

describe('ReqhandlersComponent', () => {
  let component: ReqhandlersComponent;
  let fixture: ComponentFixture<ReqhandlersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ FormsModule, SharedModule ],
      declarations: [ ReqhandlersComponent ],
      providers: [ Messages ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReqhandlersComponent);
    component = fixture.componentInstance;
    component.serviceData = new ServiceData();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
