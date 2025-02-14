---
layout: default
title: CAS - Okta Authentication
category: Authentication
---

# Okta Authentication

The integration with Okta is a convenience wrapper around [Okta's Authentication API](https://developer.okta.com/docs/api/resources/authn.html) and 
is useful if you need to accept and validate credentials managed by Okta.

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-okta-authentication</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#okta-authentication).
