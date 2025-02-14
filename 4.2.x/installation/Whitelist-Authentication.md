---
layout: default
title: CAS - Whitelist Authentication
---


# Whitelist Authentication
Whitelist authentication components fall into two categories: Those that accept a set of credentials stored directly in the configuration and those that accept a set of credentials from a file resource on the server.

These are:

- `AcceptUsersAuthenticationHandler`
- `FileAuthenticationHandler`


## Authentication Components
Support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-generic</artifactId>
  <version>${cas.version}</version>
</dependency>
```

### `AcceptUsersAuthenticationHandler`
```xml
<alias name="acceptUsersAuthenticationHandler" alias="primaryAuthenticationHandler" />
```

The following settings are applicable:

```properties
accept.authn.users=casuser::Mellon
```

### `FileAuthenticationHandler`

```xml
<alias name="fileAuthenticationHandler" alias="primaryAuthenticationHandler" />
```

The following settings are applicable:

```properties
# file.authn.filename=classpath:people.txt
# file.authn.separator=::
```

#### Example Password File
```bash
scott::password
bob::password2
```
