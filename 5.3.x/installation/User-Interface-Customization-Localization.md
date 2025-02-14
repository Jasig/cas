---
layout: default
title: CAS - User Interface Customization
---

# Localization
The CAS Web application includes a number of localized message files:

- English (US)
- Spanish
- French
- Russian
- Dutch (Netherlands)
- Swedish (Svenskt)
- Italian (Italiano)
- Urdu
- Chinese (Simplified)
- German (Deutsch)
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

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Note that not all languages are complete and accurate across CAS server releases as translations are entirely dependent upon community contributions.
For an accurate and complete list of localized messages, always refer to the English language bundle.</p></div>

## Configuration

All message bundles are marked under `messages_xx.properties` files at `src/main/resources`. The default language bundle is for the
English language and is thus called `messages.properties`. If there are any custom messages that need to be presented into views,
they may also be formatted under `custom_messages.properties` files.

In the event that the code is not found in the activated resource bundle, the code itself will be used verbatim.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#localization)
and [this guide](Configuration-Properties.html#message-bundles).
