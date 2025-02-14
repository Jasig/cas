---
layout: default
title: Views - User Interface Customization - CAS
category: User Interface
---

# Views

The views are found inside the CAS web application in the `WEB-INF\lib\cas-server-support-thymeleaf-<cas.version>.jar` in the 
templates folder. Add any views that require customization to the `src/main/resources/templates` folder in the CAS overlay project.  
Any files found in that module can be overridden by putting them in the same location under
`src/main/resources` in the CAS overlay project. The Gradle build script for the overlay has tasks that help get resources 
from the CAS web application to the correct location in the CAS overlay. 
 
Views also may be externalized outside the web application conditionally and individually, provided the external path 
via CAS settings. If a view template file is not found at the externalized path, the default one that ships with CAS will be used as the fallback.

Views may also be found using an external URL in CAS settings that is responsible to produce the full view body in 
the response. This URL endpoint will receive the available request headers as well as the following headers in its request:

| Header             
|-------------------------------------
| `owner`
| `template`
| `resource`
| `theme`, if available
| `locale`, if available

Upon a successful `200` status result, the response body is expected to contain the view that will be rendered by CAS.
 
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#views).

## Thymeleaf

CAS uses [Thymeleaf](https://www.thymeleaf.org) for its markup rendering engine. Each template is decorated by `layout.html` template file, which provides a layout structure for the template's content. Individual components optimized for re-use among multiple templates are stored in the `src/main/resources/templates/fragments` folder, and referenced by the templates in `src/main/resources/templates`.

Refer to the [Thymeleaf documentation](https://www.thymeleaf.org/) for more information on its use and syntax.

## Warning Before Accessing Application

CAS has the ability to warn the user before being redirected to the service. This allows users to be made aware whenever an application uses CAS to log them in.
(If they don't elect the warning, they may not see any CAS screen when accessing an application that successfully relies upon an existing CAS single sign-on session.)
Some CAS adopters remove the 'warn' checkbox in the CAS login view and don't offer this interstitial advisement that single sign-on is happening.

```html
...
<input id="warn"
       name="warn"
       value="true"
       tabindex="3"
       th:accesskey="#{screen.welcome.label.warn.accesskey}"
       type="checkbox" />
<label for="warn" th:utext="#{screen.welcome.label.warn}"/>
...
```      

## Custom Fields

CAS allows on the ability to dynamically extend the login form by including additional fields, to be populated by the user.
Such fields are taught to CAS using settings and are then bound to the authentication flow and made available to all
authentication handlers that wish to impose additional processes and rules using said fields.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#views).
