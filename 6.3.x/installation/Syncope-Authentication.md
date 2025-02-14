---
layout: default
title: CAS - Apache Syncope Authentication
category: Authentication
---

# Apache Syncope Authentication

CAS support handling the authentication event via [Apache Syncope](http://syncope.apache.org/). This is done by using the `rest/users/self` REST API that is exposed by a running Syncope instance. As part of a successful authentication attempt, the properties of the provided user object are transformed into CAS attributes that can then be released to applications, etc.


## Components

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-syncope-authentication</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#syncope-authentication).

## Attributes

As part of a successful authentication attempt, the following attributes provided by Apache Syncope are collected by CAS:

| Attribute Name             
|------------------------------------
| `syncopeUserRoles`
| `syncopeUserSecurityQuestion`
| `syncopeUserStatus`
| `syncopeUserRealm`
| `syncopeUserCreator`
| `syncopeUserCreationDate`
| `syncopeUserChangePwdDate`
| `syncopeUserLastLoginDate`
| `syncopeUserDynRoles`
| `syncopeUserDynRealms`
| `syncopeUserMemberships`
| `syncopeUserDynMemberships`
| `syncopeUserDynRelationships`
| `syncopeUserAttrs`

Note that attributes are only collected if they contain a value.
