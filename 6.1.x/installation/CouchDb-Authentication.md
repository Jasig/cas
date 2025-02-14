---
layout: default
title: CAS - CouchDb Authentication
---

# CouchDb Authentication

Verify and authenticate credentials against a [CouchDb](http://couchdb.apache.org/) instance via pac4j. CAS will automatically create the design documents required by pac4j.
Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-couchdb-authentication</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties,
please [review this guide](../configuration/Configuration-Properties.html#couchdb-authentication).
