---
layout: default
title: CAS - Couchbase Authentication
---

# Couchbase Authentication

Verify and authenticate credentials using [Couchbase](http://www.couchbase.com/).

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-couchbase-authentication</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#couchbase-authentication).
