---
layout: default
title: CAS - User Interface Customization
category: User Interface
---

# Views

The views are found at `src/main/resources/templates` which is a location within the CAS web application itself. 
Views also may be externalized outside the web application conditionally and individually, provided the external path 
via CAS settings. If a view template file is not found at the externalized path, the default one that ships with CAS will be used as the fallback.

Views may also be found using an external URL in CAS settings that is responsible to produce the full view body in the response. This URL endpoint will receive
the available request headers as well as the following headers in its request:

| Header             
|-------------------------------------
| `owner`
| `template`
| `resource`
| `theme`, if available
| `locale`, if available

Upon a successful `200` status result, the response body is expected to contain the view that will be rendered by CAS.
 
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#views).

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

## "I am at a public workstation" authentication

CAS has the ability to allow the user to opt-out of SSO, by indicating on the login page that the authentication
is happening at a public workstation. By electing to do so, CAS will not honor the subsequent SSO session
and will not generate the TGC that is designed to do so.

```html
...
<input id="publicWorkstation"
       name="publicWorkstation"
       value="false" tabindex="4"
       type="checkbox" />
<label for="publicWorkstation" th:utext="#{screen.welcome.label.publicstation}"/>
...
```

## Default Service

In the event that no `service` is submitted to CAS, you may specify a default
service url to which CAS will redirect. Note that this default service, much like
all other services, MUST be authorized and registered with CAS.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#views).
