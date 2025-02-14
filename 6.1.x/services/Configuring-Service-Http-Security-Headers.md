---
layout: default
title: CAS - Configuring Service HTTP Security Headers
category: Services
---

# Configure Service HTTP Security Headers

CAS has ability to control, on a per-service basis, whether certain security-related HTTP headers should be injected into the response. While headers are typically enabled and defined globally as part of the [CAS Security Filter](../planning/Security-Guide.html#cas-security-filter), the strategy described here allows one to disable/enable the injection of these headers for certain applications and service requests and override the global defaults.

A sample JSON file follows:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^https://.+",
  "name" : "sample service",
  "id" : 100,
  "properties" : {
    "@class" : "java.util.HashMap",
    "httpHeaderEnableXContentOptions" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
      "values" : [ "java.util.HashSet", [ "true" ] ]
    }
  }
}
```

Supported HTTP headers in form of service properties are:

| Header                                      | Description
|-----------------------|-----------------------------------------------------------------------
| `httpHeaderEnableCacheControl`      | Insert `Cache-Control` headers into the response for this service.
| `httpHeaderEnableXContentOptions`      | Insert `X-Content-Type-Options` headers into the response for this service.
| `httpHeaderEnableStrictTransportSecurity`   | Insert `Strict-Transport-Security` headers into the response for this service.
| `httpHeaderEnableXFrameOptions`      | Insert `X-Frame-Options` headers into the response for this service.
| `httpHeaderEnableContentSecurityPolicy`      | Insert `Content-Security-Policy` headers into the response for this service.
| `httpHeaderEnableXSSProtection`      | Insert `X-XSS-Protection` headers into the response for this service.
| `httpHeaderXFrameOptions`      | Override the `X-Frame-Options` header of the response for this service.

The headers values are picked up from CAS properties. See [this guide](../configuration/Configuration-Properties.html#http-web-requests) for relevant settings.

