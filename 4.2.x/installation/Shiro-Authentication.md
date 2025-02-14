---
layout: default
title: CAS - Shiro Authentication
---


# Shiro Authentication
CAS support handling the authentication event via [Apache Shiro](http://shiro.apache.org/). This is handled by an instance of `ShiroAuthenticationHandler`.


## Authentication Components
Support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-generic</artifactId>
  <version>${cas.version}</version>
</dependency>
```

### Shiro Configuration
Apache Shiro supports retrieving and checking roles and permissions for an authenticated
subject. CAS exposes a modest configuration to enforce roles and permissions as part
of the authentication, so that in their absence, the authentication may fail.
While by default these settings are optional, you may configure roles and/or permissions
for the given authentication handler to check their presence and report back.

```xml
<alias name="shiroAuthenticationHandler" alias="primaryAuthenticationHandler" />
```

The following settings are applicable:

```properties
# shiro.authn.requiredRoles=role1,role2
# shiro.authn.requiredPermissions=perm1,perm2
# shiro.authn.config.file=classpath:shiro.ini
```

Sample `shiro.ini` that needs be placed on the classpath based on the example above:

```ini
[main]
cacheManager = org.apache.shiro.cache.MemoryConstrainedCacheManager
securityManager.cacheManager = $cacheManager

[users]
casuser = Mellon, admin

[roles]
admin = system,admin,staff,superuser:*
```
