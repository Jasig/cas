---
layout: default
title: CAS - LDAP Authentication
---

# LDAP Authentication

LDAP integration is enabled by including the following dependency in the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-ldap</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#ldap-authentication-1).

## Password Policy Enforcement

To learn how to enforce a password policy for LDAP, please [review this guide](Password-Policy-Enforcement.html).

## Troubleshooting

To enable additional logging, modify the logging configuration file to add the following:

```xml
<AsyncLogger name="org.ldaptive" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
```
