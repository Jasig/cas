---
layout: default
title: CAS - SCIM Provisioning Integration
category: Integration
---

# Overview

The [SCIM standard](http://www.simplecloud.info/) is created to simplify user management and provisioning in the cloud by defining a schema for representing users and groups and a REST API for all the necessary CRUD operations. SCIM integrations with CAS allow deployers to auto-provision the authenticated CAS principal to a SCIM server/target with additional support to map principal attributes into the appropriate claims and properties of the user resource.

SCIM versions 1.1 and 2 are both supported, thanks to the SDK provided by [UnboundID](https://github.com/PingIdentity).

Typical use case for enabling SCIM is to synchronize and provision user accounts, just in time, to services and applications that are 
integrated with CAS for single sign-on. In cases where the application also has its own account store, a mapping of user accounts between 
the CAS canonical account store (LDAP, JDBC, etc) and the application may be required. To accommodate this issue, CAS may be allowed to 
provision the authenticated principal via SCIM to a provisioning/identity/entity engine which would then dynamically 
synchronize user profiles to target systems.

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-scim</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#provisioning).
