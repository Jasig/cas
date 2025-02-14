---
layout: default
title: CAS - Password Management
---

# Password Management - LDAP

The account password and security questions may be stored inside an LDAP server.

LDAP support is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-pm-ldap</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#ldap-password-management).