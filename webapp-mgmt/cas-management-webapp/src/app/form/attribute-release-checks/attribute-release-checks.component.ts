import {Component, OnInit, Input} from '@angular/core';
import {FormData, ServiceData} from "../../../domain/service-edit-bean";
import {Messages} from "../../messages";

@Component({
  selector: 'app-attribute-release-checks',
  templateUrl: './attribute-release-checks.component.html',
  styleUrls: ['./attribute-release-checks.component.css']
})
export class AttributeReleaseChecksComponent implements OnInit {
  @Input()
  serviceData: ServiceData;

  @Input()
  formData: FormData;

  @Input()
  selectOptions;

  constructor(public messages: Messages) { }

  ngOnInit() {
  }

  isEmpty(data: any[]) {
    return data != null && data.length == 0;
  }

}
