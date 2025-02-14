---
layout: default
title: CAS - Google reCAPTCHA
---

# Google reCAPTCHA

reCAPTCHA is a [Google service](https://developers.google.com/recaptcha) that protects your CAS deployment from spam and abuse.
It uses advanced risk analysis techniques to tell humans and bots apart.

Support is enabled by including the following module in the Overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-captcha</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#google-recaptcha-integration).

## Internet Explorer

Remember to disable Internet Explorer's "Compatibility View" mode. reCAPTCHA does not render correctly when that mode is turned on.
