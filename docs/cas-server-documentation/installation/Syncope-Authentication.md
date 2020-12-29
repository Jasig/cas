---
layout: default
title: CAS - Apache Syncope Authentication
category: Authentication
---
{% include variables.html %}


# Apache Syncope Authentication

CAS support handling the authentication event via [Apache Syncope](http://syncope.apache.org/). This 
is done by using the `rest/users/self` REST API that is exposed by a running Syncope instance. 
As part of a successful authentication attempt, the properties of the provided user object 
are transformed into CAS attributes that can then be released to applications, etc.

## Components

Support is enabled by including the following dependency in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-syncope-authentication" %}

{% include {{ version }}/principal-transformation-configuration.md configKey="cas.authn.syncope" %}

{% include {{ version }}/password-encoding-configuration.md configKey="cas.authn.syncope" %}

{% include {{ version }}/syncope-authentication-configuration.md %}

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
