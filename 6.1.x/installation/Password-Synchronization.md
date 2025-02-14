---
layout: default
title: CAS - Password Synchronization
category: Authentication
---

# Password Synchronization

CAs presents the ability to synchronize and update the account password in a variety of
destinations as part of the authentication event. If the authentication attempt is successful,
CAS will attempt to capture the provided password and update destinations that are specified
in CAS settings. Failing to synchronize an account password generally produces errors in the logs
and the event is not considered a catastrophic failure.

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#password-synchronization).

## LDAP

Synchronize account passwords with one or more LDAP servers. Support is enabled by including the 
following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-ldap</artifactId>
    <version>${cas.version}</version>
</dependency>
```
