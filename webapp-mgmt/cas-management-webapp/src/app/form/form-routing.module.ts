/**
 * Created by tschmidt on 2/23/17.
 */
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {FormComponent} from "./form.component";
import {FormResolve} from "./form.resolover";
import {TabBasicsComponent} from "./tab-basics/tab-basics.component";
import {TabServicetypeComponent} from "./tab-servicetype/tab-servicetype.component";
import {TabLogoutComponent} from "./tab-logout/tab-logout.component";
import {TabAccessstrategyComponent} from "./tab-accessstrategy/tab-accessstrategy.component";
import {TabMulitauthComponent} from "./tab-mulitauth/tab-mulitauth.component";
import {TabProxyComponent} from "./tab-proxy/tab-proxy.component";
import {TabUsernameattrComponent} from "./tab-usernameattr/tab-usernameattr.component";
import {TabAttrreleaseComponent} from "./tab-attrrelease/tab-attrrelease.component";
import {TabPropertiesComponent} from "./tab-properties/tab-properties.component";
import {TabAdvancedComponent} from "./tab-advanced/tab-advanced.component";

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'form/:id',
        component: FormComponent,
        resolve: {
          resp: FormResolve
        },
        children: [
          {
            path: 'basics',
            component: TabBasicsComponent,
            outlet: 'form'
          },
          {
            path: 'servicetype',
            component: TabServicetypeComponent,
            outlet: 'form'
          },
          {
            path: 'logout',
            component: TabLogoutComponent,
            outlet: 'form'
          },
          {
            path: 'accessstrategy',
            component: TabAccessstrategyComponent,
            outlet: 'form'
          },
          {
            path: 'multiauth',
            component: TabMulitauthComponent,
            outlet: 'form'
          },
          {
            path: 'proxy',
            component: TabProxyComponent,
            outlet: 'form'
          },
          {
            path: 'userattr',
            component: TabUsernameattrComponent,
            outlet: 'form'
          },
          {
            path: 'attrRelease',
            component: TabAttrreleaseComponent,
            outlet: 'form'
          },
          {
            path: 'properties',
            component: TabPropertiesComponent,
            outlet: 'form'
          },
          {
            path: 'advanced',
            component: TabAdvancedComponent,
            outlet: 'form'
          },
        ]
      }
    ])
  ],
  exports: [ RouterModule ]
})

export class FormRoutingModule {}

