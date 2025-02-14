---
layout: default
title: CAS - Google reCAPTCHA
category: Integration
---

# Google reCAPTCHA

reCAPTCHA is a [Google service](https://developers.google.com/recaptcha) that protects your CAS deployment from spam and abuse.
It uses advanced risk analysis techniques to tell humans and bots apart. CAS supports the reCAPTCHA API `v2` and `v3`.

Support is enabled by including the following module in the Overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-captcha</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#google-recaptcha-integration).

## Internet Explorer

Remember to disable Internet Explorer's "Compatibility View" mode. reCAPTCHA may not render correctly when that mode is turned on.
