---
layout: default
title: CAS - User Interface Customization
---


# Overview
Branding the CAS User Interface (UI) involves simply editing the CSS stylesheet and also a small collection of relatively simple JSP include files,
also known as views. Optionally, you may also wish to modify the text displayed and/or add additional Javascript effects on these views.

All the files that we'll be discussing in this section that concern the theme are located in and referenced from: `/cas-server-webapp/src/main/webapp`.

# Browser Support
CAS user interface should properly and comfortably lend itself to all major browser vendors:

* Google Chrome
* Mozilla Firefox
* Apple Safari
* Microsoft Internet Explorer

Note that certain older version of IE, particularly IE 9 and below may impose additional difficulty in getting the right UI configuration in place.

## Internet Explorer
To instruct CAS to render UI in compatibility mode, add the following to relevant UI components:

```html
<meta http-equiv="X-UA-Compatible" content="IE=edge"></meta>
```

# Getting Started

## CSS
The default styles are all contained in a single file located in `css/cas.css`. This location is set in `WEB-INF/classes/cas-theme-default.properties`. If you would like to create your own `css/custom.css file`, for example, you will need to update `standard.custom.css.file` key in that file.

```bash
standard.custom.css.file=/css/cas.css
cas.javascript.file=/js/cas.js
```


### CSS per Locale
Selecting CSS files per enabled locale would involve changing the `top.jsp` file to include the below sample code:

```html
<%
    String cssFileName = "cas.css"; // default
    Locale locale = request.getLocale();

    if (locale != null && locale.getLanguage() != null){
       String languageCssFileName = "cas_" + locale.getLanguage() + ".css";
       cssFileName = languageCssFileName; //ensure this file exists
    }

%>
<link href="/path/to/css/<%=cssFileName%>" rel="stylesheet" type="text/css"/>
```


### Responsive Design
CSS media queries bring responsive design features to CAS which would allow adopter to focus on one theme for all appropriate devices and platforms. These queries are defined in the same `css/cas.css` file.


## Javascript
If you need to add some JavaScript, feel free to append `js/cas.js`.

You can also create your own `custom.js` file, for example, and call it from within `WEB-INF/view/jsp/default/ui/includes/bottom.jsp` like so:


```html
<script type="text/javascript" src="<c:url value="/js/custom.js" />"></script>
```

If you are developing themes per service, each theme also has the ability to specify a custom `cas.js` file under the `cas.javascript.file` setting.

The following Javascript libraries are utilized by CAS automatically:

* JQuery
* JQuery UI
* JQuery Cookie
* [JavaScript Debug](http://benalman.com/projects/javascript-debug-console-log/): A simple wrapper for `console.log()`

### Asynchronous Script Loading
CAS will attempt load the aforementioned script libraries asynchronously so as to not block the page rendering functionality.
The loading of script files is handled by the [`head.js` library](http://headjs.com) and is the responsibility of `cas.js` file.

The only script that is loaded synchronously is the `head.js` library itself.

Because scripts, and specially JQuery are loaded asynchronously, any custom Javascript that is placed inside the page
that relies on these libraries may not immediately function on page load. CAS provides a callback function that allows
adopters to be notified when script loading has completed and this would be a safe time to execute/load other Javascript-related
functions that depend on JQuery inside the actual page.

```javascript
function jqueryReady() {
    //Custom Javascript tasks can be carried out now via JQuery...
}
```


### Checking CAPSLOCK
CAS will display a brief warning when the CAPSLOCK key is turned on during the typing of the credential password. This check is enforced by the `cas.js` file.

### Browser Cookie Support
For CAS to honor a single sign-on session, the browser MUST support and accept cookies. CAS will notify the
user if the browser has turned off its support for cookies. This behavior is controlled via the `cas.js` file.

### Preserving Anchor Fragments
Anchors/fragments may be lost across redirects as the server-side handler of the form post ignores the client-side anchor, unless appended to the form POST url.
This is needed if you want a CAS-authenticated application to be able to use anchors/fragments when bookmarking.

#### Changes to `cas.js`
```javascript
/**
 * Prepares the login form for submission by appending any URI
 * fragment (hash) to the form action in order to propagate it
 * through the re-direct (i.e. store it client side).
 * @param form The login form object.
 * @returns true to allow the form to be submitted.
 */
function prepareSubmit(form) {
    // Extract the fragment from the browser's current location.
    var hash = decodeURIComponent(self.document.location.hash);

    // The fragment value may not contain a leading # symbol
    if (hash && hash.indexOf("#") === -1) {
        hash = "#" + hash;
    }

    // Append the fragment to the current action so that it persists to the redirected URL.
    form.action = form.action + hash;
    return true;
}
```


#### Changes to Login Form

```html
<form:form method="post" id="fm1" cssClass="fm-v clearfix"
        commandName="${commandName}" htmlEscape="true"
        onsubmit="return prepareSubmit(this);">
```

## JSP
The default views are found at `WEB-INF/view/jsp/default/ui/`.

Notice `top.jsp` and `bottom.jsp` include files located in the `../includes` directory. These serve as the layout template for the other JSP files, which get injected in between during compilation to create a complete HTML page.

#### Tag Libraries
The following JSP tag libraries are used by the user interface:

```html
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
```


#### Glossary of Views

| View                             | Description
|-----------------------------------+--------------------------------------------------------------------------------+
| `casAccountDisabledView`  | Specific to Password Policy Enforcement; displayed in the event that authentication encounters an account that is disabled in the underlying account store (i.e. LDAP)
| `casAccountLockedView`    | Specific to Password Policy Enforcement; displayed in the event that authentication encounters an account that is locked in the underlying account store (i.e. LDAP)
| `casBadHoursView`         | Specific to Password Policy Enforcement; displayed when authentication encounters an account that is not allowed authentication within the current time window in the underlying account store (i.e. LDAP)
| `casBadWorkstationView`   | Specific to Password Policy Enforcement; displayed when authentication encounters an account that is not allowed authentication from the current workstation in the underlying account store (i.e. LDAP)
| `casExpiredPassView`  | Specific to Password Policy Enforcement; displayed in the event that authentication encounters an account that has expired in the underlying account store (i.e. LDAP)
| `casMustChangePassView`  | Specific to Password Policy Enforcement; displayed in the event that authentication encounters an account that must change its password in the underlying account store (i.e. LDAP)
| `casWarnPassView` | Specific to Password Policy Enforcement; displayed when the user account is near expiration based on specified configuration (i.e. LDAP)
| `casConfirmView`  | Displayed when the user is warned before being redirected to the service.  This allows users to be made aware whenever an application uses CAS to log them in. (If they don't elect the warning, they may not see any CAS screen when accessing an application that successfully relies upon an existing CAS single sign-on session.) Some CAS adopters remove the 'warn' checkbox in the CAS login view and don't offer this interstitial advisement that single sign-on is happening.
| `casGenericSuccess` | Displayed when the user has been logged in without providing a service to be redirected to.
| `casLoginView`  | Main login form.
| `casLogoutView` | Main logout view.
| `serviceErrorView` | Used in conjunction with the service registry feature, displayed when the service the user is trying to access is not allowed to use CAS. The default in-memory services registry configuration, in 'deployerConfigContext.xml', allows all users to obtain a service ticket to access all services.
| `serviceErrorSsoView` | Displayed when a user would otherwise have experienced non-interactive single sign-on to a service that is, per services registry configuration, disabled from participating in single sign-on. (In the default services registry registrations, all services are permitted to participate in single sign-on, so this view will not be displayed.)


#### Glossary of Monitoring Views
The monitoring views are found at `WEB-INF/view/jsp/monitoring/`.


| View                             | Description
|-----------------------------------+--------------------------------------------------------------------------------+
| `viewConfig`  | Displayed when user attempts to view the state of the CAS application runtime and its configuration.
| `viewSsoSessions` | Displayed when user wishes to view the Single Sign-on Report.
| `viewStatistics`  | Displayed when user wishes review the CAS server statistics.


#### Glossary of System Error Views
The error views are found at `WEB-INF/view/jsp/`.

| View                             | Description
|-----------------------------------+--------------------------------------------------------------------------------+
| `errors`  | Displayed when CAS experiences an error it doesn't know how to handle (an unhandled Exception). For instance, CAS might be unable to access a database backing the services registry. This is the generic CAS error page. It's important to brand it to provide an acceptable error experience to your users.
| `authorizationFailure` | Displayed when a user successfully authenticates to the services management web-based administrative UI included with CAS, but the user is not authorized to access that application.

### Warning Before Accessing Application
CAS has the ability to warn the user before being redirected to the service. This allows users to be made aware whenever an application uses CAS to log them in.
(If they don't elect the warning, they may not see any CAS screen when accessing an application that successfully relies upon an existing CAS single sign-on session.)
Some CAS adopters remove the 'warn' checkbox in the CAS login view and don't offer this interstitial advisement that single sign-on is happening.

```html
...
<input id="warn" name="warn" value="true" tabindex="3" accesskey="<spring:message code="screen.welcome.label.warn.accesskey" />" type="checkbox" />
<label for="warn"><spring:message code="screen.welcome.label.warn" /></label>
...
```

### "I am at a public workstation" authentication
CAS has the ability to allow the user to opt-out of SSO, by indicating on the login page that the authentication
is happening at a public workstation. By electing to do so, CAS will not honor the subsequent SSO session
and will not generate the TGC that is designed to do so.

```html
...
<input id="publicWorkstation" name="publicWorkstation" value="false" tabindex="4" type="checkbox" />
<label for="publicWorkstation"><spring:message code="screen.welcome.label.publicstation" /></label>
...
```

## Localization
The CAS Web application includes a number of localized message files:

- English (US)
- Spanish
- French
- Russian
- Netherlands (Nederlands)
- Swedish (Svenskt)
- Italian (Italiano)
- Urdu
- Chinese (Simplified)
- Dutch (Deutsch)
- Japanese
- Croatian
- Czech
- Slovenian
- Polish
- Portuguese (Brazil)
- Turkish
- Farsi
- Arabic

In order to "invoke" a specific language for the UI, the `/login` endpoint may be passed a `locale` parameter as such:

```html
https://cas.server.edu/login?locale=it
```

Note that not all languages are complete and accurate across CAS server releases as translations are entirely dependent upon community contributions.
For an accurate and complete list of localized messages, always refer to the English language bundle.

### Configuration
All message bundles are marked under `messages_xx.properties` files at `WEB-INF/classes`. The default language bundle is for the
English language and is thus called `messages.properties`. If there are any custom messages that need to be presented into views,
they may also be formatted under `custom_messages.properties` files.

Messages are then read on each JSP view via the following sample configuration:

```html
<spring:message code="message.key" />
```

In the event that the code is not found in the activated resource bundle, the code itself will be used verbatim.


## Themes
With the introduction of [Service Management application](Service-Management.html), deployers are now able to switch the themes based on different services. For example, you may want to have different login screens (different styles) for staff applications and student applications. Or, you want to show two layouts for day time and night time. This document could help you go through the basic settings to achieve this.

Note that support for themes comes with the following components:

| Component                      | Description
|--------------------------------+--------------------------------------------------------------------------------+
| `ServiceThemeResolver`  | can be configured to decorate CAS views based on the `theme` property of a given registered service in the Service Registry. The theme that is activated via this method will still preserve the default JSP views for CAS but will simply apply decorations such as CSS and Javascript to the views. The physical structure of views cannot be modified via this method.
| `RegisteredServiceThemeBasedViewResolver` | If there is a need to present an entirely new set of views for a given service, such that the structure and layout of the page needs an overhaul with additional icons, images, text, etc then this component` needs to be configured. This component will have the ability to resolve a new set of views that may entirely be different from the default JSPs. The `theme` property of a given registered service in the Service Registry will still need to be configured to note the set of views that are to be loaded.


### Dynamic
Configuration of service-specific themes is backed by the Spring framework and provided by the following component:

Furthermore, deployers may be able to use the functionality provided by the `ThemeChangeInterceptor` of 
Spring framework to provide theme configuration per each request.

#### Configuration
- Add another theme properties file, which must be placed to the root of `/WEB-INF/classes` folder, name it as `theme_name.properties`. 
Contents of this file should match the `cas-theme-default.properties` file.
- Add the location of related styling files, such as CSS and Javascript in the file above.
- Specify the name of your theme for the service definition under the `theme` property.

### Per Application
Alternatively, CAS utilizes a service's
associated theme to selectively choose which set of UI views will be used to generate the standard views (`casLoginView.jsp`, etc). 
This is specially useful in cases where the set of pages for a theme that are targeted
for a different type of audience are entirely different structurally that simply
using the dynamic mode is not practical to augment the default views. In such cases, new view pages may be required.


#### Configuration
- Clone the default set of view pages into a new directory based on the theme id (i.e. `/WEB-INF/view/jsp/<theme-id>/ui/`).
- Specify the name of your theme for the service definition under the `theme` property.
