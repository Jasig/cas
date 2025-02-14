---
layout: default
title: CAS - Stormpath Authentication
---

# Stormpath Authentication

Verify and authenticate credentials against the [Stormpath](https://stormpath.com/) Cloud Identity.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Functionality provided by Stormpath is not longer available.</p></div>

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-stormpath</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#stormpath-authentication).
