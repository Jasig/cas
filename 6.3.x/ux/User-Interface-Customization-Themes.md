---
layout: default
title: Dynamic Themes - User Interface Customization - CAS
category: User Interface
---

# Dynamic Themes

With the introduction of [Service Management application](../services/Service-Management.html), deployers are now able to switch the themes based on 
different services. For example, you may want to have different login screens (different styles) for staff applications and student applications, or you want to show two 
layouts for day time and night time. This document could help you go through the basic settings to achieve this.

## Static Themes

CAS is configured to decorate views based on the `theme` property of a given registered service in the Service Registry. The theme that is activated via this method will still preserve the default views for CAS but will apply decorations such as CSS and Javascript to the views. The physical structure of views cannot be modified via this method.

### Configuration

- Add a `[theme_name].properties` placed to the root of `src/main/resources` folder. 
Contents of this file should match the following:

```properties 
# Path to theme CSS file
cas.standard.css.file=/themes/[theme_name]/css/cas.css

# Path to theme JS file
cas.standard.js.file=/themes/[theme_name]/js/cas.js

# Path to theme logo to display via the common layout
# cas.logo.file=/images/logo.png     

# Decide whether drawer menu should be displayed
# cas.drawer-menu.enabled=true                    

# Theme name used in various titles/captions
# cas.theme.name=Example Theme

# Path to theme favicon file.
# cas.favicon.file=/themes/example/images/favicon.ico

# Enable and display the notifications menu
# cas.notifications-menu.enabled=true

# Enable and display the drawer menu
# cas.drawer-menu.enabled=true
```

- Create the directory `src/main/resources/static/themes/[theme_name]`. Put the theme-specific `cas.css` and `cas.js` inside the appropriate directories for `css` and `js`.
- Specify `[theme_name]` for the service definition under the `theme` property.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^https://www.example.org",
  "name" : "MyTheme",
  "theme" : "[theme_name]",
  "id" : 1000
}
```

Values can use the [Spring Expression Language](../configuration/Configuration-Spring-Expressions.html) syntax.

## Themed Views

CAS can also utilize a service's associated theme to selectively choose which set of UI views will be used to generate 
the standard views (`casLoginView.html`, etc). This is specially useful in cases where the set of pages for a theme that are targeted 
for a different type of audience are entirely different structurally that using a simple theme is not practical to augment the default views. In such cases, new view pages may be required.

Views associated with a particular theme by default are expected to be found at: `src/main/resources/templates/<theme-id>`. Note that CAS 
views and theme-based views may both be externalized out of the web application context. When externalized, themed views are expected to be found at the specified path via CAS properties under a 
directory named after the theme. For instance, if the external path for CAS views is `/etc/cas/templates`, view template files for 
theme `sample` may be located `/etc/cas/templates/sample/`.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#views).

### Configuration

- Add a `[theme_name].properties` placed to the root of `src/main/resources` folder. Contents of this file should match the following:

```properties
cas.standard.css.file=/themes/[theme_name]/css/cas.css
cas.standard.js.file=/themes/[theme_name]/js/cas.js
```

- Clone the default set of view pages into a new directory based on the theme id (i.e. `src/main/resources/templates/<theme-id>`).
- Specify the name of your theme for the service definition under the `theme` property.

## Theme Collections

CAS provides a module that presents a number of ready-made themes. The intention for each themes
to account for common and provide for common use cases when it comes to user interface modifications
and samples, and attempt to automate much of the configuration.

Support is enabled by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-theme-collections</artifactId>
     <version>${cas.version}</version>
</dependency>
```       

The following themes are provided by this module and can be assigned to service definitions:

| Theme              | Description    
|--------------------|----------------------------------------------------------------------------
| `example`          | A reference example theme that combines customized CSS, Javascript and views

The collection of themes above can also serve as reference examples of how to define a theme with
custom CSS, Javascript and associated views and fragments.

## Groovy Themes

If you have multiple themes defined, it may be desirable to dynamically determine a theme for a given service definition. In order to do, you may calculate the final theme name via a Groovy script of your own design. The theme assigned to the service definition needs to point to the location of the script:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^https://www.example.org",
  "name" : "MyTheme",
  "theme" : "file:///etc/cas/config/themes.groovy",
  "id" : 1000
}
```

The script itself may be designed as:

```groovy
import java.util.*

def String run(final Object... args) {
    def service = args[0]
    def registeredService = args[1]
    def queryStrings = args[2]
    def headers = args[3]
    def logger = args[4]

    // Determine theme ...

    return null
}
```

Returning `null` or blank will have CAS switch to the default theme. The following parameters may be passed to a Groovy script:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `service`             | The object representing the requesting service.
| `registeredService`   | The object representing the matching registered service in the registry.
| `queryStrings`        | Textual representation of all query strings found in the request, if any.
| `headers`             | `Map` of all request headers and their values found in the request, if any.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.

## RESTful Themes

Somewhat similar to the above option, you may calculate the final theme name via a REST endpoint of your own design. The theme assigned to the service definition needs to point to the location of the REST API. Endpoints must be designed to accept/process `application/json` via `GET` requests. A returned status code `200` allows CAS to read the body of the response to determine the theme name. Empty response bodies will have CAS switch to the default theme.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^https://www.example.org",
  "name" : "MyTheme",
  "theme" : "https://themes.example.org",
  "id" : 1000
}
```

The following parameters may be passed to a Groovy script:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `service`             | The requesting service identifier.
