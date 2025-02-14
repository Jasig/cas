---
layout: default
title: CAS - Whitelist Authentication
category: Authentication
---

# Whitelist Authentication

Whitelist authentication components fall into two categories: Those that accept a set of credentials stored directly in the configuration and those that accept a set of credentials from a file resource on the server.

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-generic</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#file-whitelist-authentication).

## Example Password File

```bash
scott::password
bob::password2
```


## JSON File

The password file may also be specified as a JSON resource instead which allows one to specify additional account details mostly useful for development and basic testing. The outline of the file may be defined as:

```json
{
  "@class" : "java.util.LinkedHashMap",
  "casuser" : {
    "@class" : "org.apereo.cas.adaptors.generic.CasUserAccount",
    "password" : "Mellon",
    "attributes" : {
      "@class" : "java.util.LinkedHashMap",
      "firstName" : [ "java.util.List", ["Apereo"]],
      "lastName" : [ "java.util.List", ["CAS"]]
    },
    "status" : "OK",
    "expirationDate" : "2050-01-01"
  }
}
```

The accepted statuses are `OK`, `LOCKED`, `DISABLED`, `EXPIRED` and `MUST_CHANGE_PASSWORD`. To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#json-whitelist-authentication).
