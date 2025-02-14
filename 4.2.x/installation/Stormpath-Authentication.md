---
layout: default
title: CAS - Stormpath Authentication
---

# Stormpath Authentication
Verify and authenticate credentials against the [Stormpath](https://stormpath.com/) Cloud Identity.

```xml
<alias name="stormpathAuthenticationHandler" alias="primaryAuthenticationHandler" />
```

Support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-stormpath</artifactId>
  <version>${cas.version}</version>
</dependency>
```

The following settings are applicable:

```properties
cas.authn.stormpath.api.key=
cas.authn.stormpath.app.id=
cas.authn.stormpath.secret.key=
```
