import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AttributeReleasePrincipalRepoComponent } from './attribute-release-principal-repo.component';
import {SharedModule} from "../../shared/shared.module";
import {FormModule} from "../form.module";
import {FormsModule} from "@angular/forms";
import {Messages} from "../../messages";
import {ServiceData, FormData} from "../../../domain/service-edit-bean";
import {TabService} from "../tab.service";

describe('AttributeReleasePrincipalRepoComponent', () => {
  let component: AttributeReleasePrincipalRepoComponent;
  let fixture: ComponentFixture<AttributeReleasePrincipalRepoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, SharedModule],
      declarations: [ AttributeReleasePrincipalRepoComponent ],
      providers: [Messages]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AttributeReleasePrincipalRepoComponent);
    component = fixture.componentInstance;
    component.formData = new FormData;
    component.serviceData = new ServiceData();
    component.selectOptions = new TabService().selectOptions;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
