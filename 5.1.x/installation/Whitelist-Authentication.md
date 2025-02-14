---
layout: default
title: CAS - Whitelist Authentication
---

# Whitelist Authentication

Whitelist authentication components fall into two categories:
Those that accept a set of credentials stored directly in the configuration and those
that accept a set of credentials from a file resource on the server.

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-generic</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#file-whitelist-authentication).

## Example Password File

```bash
scott::password
bob::password2
```
