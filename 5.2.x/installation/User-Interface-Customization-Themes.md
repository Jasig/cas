---
layout: default
title: CAS - User Interface Customization
---

# Dynamic Themes

With the introduction of [Service Management application](Service-Management.html), deployers are now able to switch the themes based on different services. For example, you may want to have different login screens (different styles) for staff applications and student applications. Or, you want to show two layouts for day time and night time. This document could help you go through the basic settings to achieve this.

## Static Themes

CAS is configured to decorate views based on the `theme` property of a given registered service in the Service Registry. The theme that is activated via this method will still preserve the default views for CAS but will simply apply decorations such as CSS and Javascript to the views. The physical structure of views cannot be modified via this method.

### Configuration

- Add a `[theme_name].properties` placed to the root of `src/main/resources` folder. Contents of this file should match the following:

```properties
standard.custom.css.file=/themes/[theme_name]/css/cas.css
cas.javascript.file=/themes/[theme_name]/js/cas.js
admin.custom.css.file=/themes/[theme-name]/css/admin.css
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

## Themed Views

CAS can also utilize a service's associated theme to selectively choose which set of UI views will be used to generate the standard views (`casLoginView.html`, etc). This is specially useful in cases where the set of pages for a theme that are targeted for a different type of audience are entirely different structurally that simply using a simple theme is not practical to augment the default views. In such cases, new view pages may be required.

Views associated with a particular theme by default are expected to be found at: `src/main/resources/templates/<theme-id>`

### Configuration

- Add a `[theme_name].properties` placed to the root of `src/main/resources` folder. Contents of this file should match the following:

```properties
# must have a least one line to active the theme
standard.custom.css.file=/themes/[theme_name]/css/cas.css
cas.javascript.file=/themes/[theme_name]/js/cas.js
admin.custom.css.file=/themes/[theme-name]/css/admin.css
```

- Clone the default set of view pages into a new directory based on the theme id (i.e. `src/main/resources/templates/<theme-id>`).
- Specify the name of your theme for the service definition under the `theme` property.

PS: The file `[theme_name].properties` will activate the theme in CAS, so it has to exist in order for the theme view to be functional.

## Groovy Themes

If you have multiple themes defined, it may be desireable to dynamically determine a theme for a given service definition. In order to do, you may calculate the final theme name via a Groovy script of your own design. The theme assigned to the service definition needs to point to the location of the script:

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
