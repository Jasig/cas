---
layout: default
title: CAS - Database Authentication
---

# Database Authentication

Database authentication is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To learn how to configure database drivers, [please see this guide](JDBC-Drivers.html).

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#database-authentication).

## Password Policy Enforcement

A certain number of database authentication schemes have limited support for detecting locked/disabled/etc accounts
via column names that are defined in the CAS sttings. To learn how to enforce a password policy, please [review this guide](Password-Policy-Enforcement.html).
