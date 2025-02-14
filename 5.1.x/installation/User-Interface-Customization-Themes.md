---
layout: default
title: CAS - User Interface Customization
---

# Dynamic Themes
With the introduction of [Service Management application](Service-Management.html), deployers are now able to switch the themes based on different services. For example, you may want to have different login screens (different styles) for staff applications and student applications. Or, you want to show two layouts for day time and night time. This document could help you go through the basic settings to achieve this.

## Themes

CAS is configured to decorate views based on the `theme` property of a given registered service in the Service Registry. The theme that is activated via this method will still preserve the default views for CAS but will simply apply decorations such as CSS and Javascript to the views. The physical structure of views cannot be modified via this method.

### Configuration

- Add a `[theme_name].properties` placed to the root of `src/main/resources` folder.

- Contents of this file should match the following:

```properties
standard.custom.css.file=/themes/[theme_name]/css/cas.css
cas.javascript.file=/themes/[theme_name]/js/cas.js
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

CAS can also utilize a service's associated theme to selectively choose which set of UI views will be used to generate the standard views (`casLoginView.html`, etc). This is specially useful in cases where the set of pages for a theme that are targeted
for a different type of audience are entirely different structurally that simply
using a simple theme is not practical to augment the default views. In such cases, new view pages may be required.

Views associated with a particular theme by default are expected to be found at: `src/main/resources/templates/<theme-id>`

### Configuration
- Clone the default set of view pages into a new directory based on the theme id (i.e. `src/main/resources/templates/<theme-id>`).
- Specify the name of your theme for the service definition under the `theme` property.
