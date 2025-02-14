---
layout: default
title: CAS - Trusted Authentication
---

# Trusted Authentication

The trusted authentication handler provides support for trusting authentication performed by some other component
in the HTTP request handling chain. Proxies (including Apache in a reverse proxy scenario) are the most common
components that perform authentication in front of CAS.

Trusted authentication handler support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-trusted-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

Trusted authentication is able to extract the remote authenticated user via the following ways:

1. Username may be extracted from `HttpServletRequest#getRemoteUser()`
2. Username may be extracted from `HttpServletRequest#getUserPrincipal()`
3. Username may be extracted from a request header whose name is defined in CAS settings.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#trusted-authentication).
