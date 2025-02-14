---
layout: default
title: CAS - Surrogate Authentication
---

# Surrogate Authentication

Surrogate authentication (impersonation), sometimes known as *sudo for the web*, is the ability to authenticate on behalf of another user. 

The two actors in this case are:

1. The primary admin user whose credentials are verified upon authentication.
2. The surrogate user, selected by the admin, to which CAS will switch after credential verification and is one that is linked to the single sign-on session.

Example use cases for impersonation include:

1. Logging into an application on behalf of a user to execute and make changes.
2. Troubleshoot a bothersome authentication experience with an application on behalf of another user.

Surrogate authentication is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-surrogate-webflow</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Account Storage

The following account stores may be configured and used to locate surrogates authorized for a particular user.

### Static

Surrogate accounts may be defined statically in the CAS configuration. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#surrogate-authentication).

### JSON

Similar to above, except that surrogate accounts may be defined in an external JSON file whose path is specified via the CAS configuration. The syntax of the JSON file should match the following snippet:

```json
{
    "casuser": ["jsmith", "banderson"],
    "adminuser": ["jsmith", "tomhanks"]
}
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#surrogate-authentication).

### LDAP

LDAP support for surrogate authentication is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-surrogate-authentication-ldap</artifactId>
    <version>${cas.version}</version>
</dependency>
```

Surrogate accounts may also be retrieved from an LDAP instance. Such accounts are expected to be found in a configured attribute defined for the primary user in LDAP whose value(s) may be examined against a regular expression pattern of your own choosing to further narrow down the list of authorized surrogate accounts. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#surrogate-authentication).

### JDBC

JDBC support for surrogate authentication is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-surrogate-authentication-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
```

Aside from the usual database settings, this mode requires the specification of two SQL queries; one that determines eligibility and one that is able to retrieve
the list of accounts that can be impersonated for a given admin user. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#jdbc-surrogate-accounts).

### REST

REST support for surrogate authentication is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-surrogate-authentication-rest</artifactId>
    <version>${cas.version}</version>
</dependency>
```

| Method       | Description                                                   | Parameter(s)             | Response
|--------------|---------------------------------------------------------------|--------------------------|-----------
| `GET`        | Whether principal can authenticate as a surrogate account.    | `surrogate`, `principal` | `202`
| `GET`        | List of accounts principal is eligible to impersonate.        | `principal` | JSON list of usernames.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#rest-surrogate-accounts).

### Custom

If you wish to design your own account store, you may follow the below approach:

```java
package org.apereo.cas.custom;

@Configuration("mySurrogateConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MySurrogateConfiguration {

    @Bean
    public SurrogateAuthenticationService surrogateAuthenticationService() {
      ...
    }

}
```

[See this guide](Configuration-Management-Extensions.html) to learn more about how to register configurations into the CAS runtime.

## Account Selection

The surrogate user selection can happen via the following ways.

### Preselected

This is the case where the surrogate user identity is known beforehand and is provided to CAS upon login using a special syntax.
When entering credentials, the following syntax should be used:

```bash
[surrogate-userid][separator][primary-userid]
```

For example, if you are `casuser` and you need to switch to `jsmith` as the surrogate user, the credential id provided to CAS would be `jsmith+casuser` where the separator is `+` and can be altered via the CAS configuration. You will need to provide your own password of course.

### GUI

This is the case where the surrogate user identity is *not* known beforehand, and you wish to choose the account from a prepopulated list. When entering credentials, the following syntax should be used:

```bash
[separator][primary-userid]
```

For example, if you are `casuser` and you need to locate the surrogate account to which you may want to switch, the credential id provided to CAS would be `+casuser` where the separator is `+` and can be altered via the CAS configuration. You will need to provide your own password of course.

## Session Expiration

An impersonation session can be assigned a specific expiration policy that would control how long a surrogate session may last. This means that the SSO session established as part of impersonation will rightly vanish, once the expiration policy dictates as such. It is recommended that you keep the expiration length short (i.e. 30 minutes) to avoid possible security issues.

## Surrogate Attributes

Upon a successful surrogate authentication event, the following attributes are communicated back to the application in order to detect an impersonation session:

| Attribute             | Instructions
|-----------------------|-------------------------------------------------------------------------------
| `surrogateEnabled`    | Boolean to indicate whether session is impersonated.
| `surrogatePrincipal`  | The admin user whose credentials are validated and acts as the impersonator.
| `surrogateUser`       | The surrogate user that is impersonated.

## Surrogate Access Strategy

Each surrogate account storage is able to determine the list of impersonatees to enforce authorization rules. Additionally, you may on a per-service level define whether an application is authorized to leverage surrogate authentication. The surrogate access strategy is only activated if the establish authentication and SSO session is one of impersonation.

See below for the available options.

### Attributes

Decide whether the primary user is tagged with enough attributes and entitlements to allow impersonation to execute. In the below example, surrogate access to the application matching `testId` is allowed only if the authenticated primary user carries an attribute `givenName` which contains a value of `Administrator`.

A sample service definition follows:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "accessStrategy" : {
    "@class" : "org.apereo.cas.services.SurrogateRegisteredServiceAccessStrategy",
    "surrogateEnabled" : true,
    "enabled": true,
    "ssoEnabled": true,
    "surrogateRequiredAttributes" : {
      "@class" : "java.util.HashMap",
      "givenName" : [ "java.util.HashSet", [ "Administrator" ] ]
    }
  }
}
```

### Groovy

Decide whether the primary user is allowed to go through impersonation via an external Groovy script. A sample service file follows:


```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "accessStrategy" : {
    "@class" : "org.apereo.cas.services.GroovySurrogateRegisteredServiceAccessStrategy",
    "groovyScript": "file:/etc/cas/config/surrogate.groovy"
  }
}
```

The Groovy script itself may be designed as:

```groovy
import java.util.*

def Object run(final Object... args) {
    def principal = args[0]
    def principalAttributes = args[1]
    def logger = args[2]

    logger.info("Checking for impersonation authz for $principal...")

    // Decide if impersonation is allowed by returning true...
    if (principal.equals("casuser")) {
        return true
    }
    logger.warn("User is not allowed to proceed with impersonation!")
    return false
}
```

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|-------------------------------------------------------------------------------------------
| `principal`             | Primary/Principal user id.
| `principalAttributes`   | Principal attributes collected for the primary user.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.

## Surrogate Audits

Surrogate authentication events are by default tracked in the audit logs:

```
=============================================================
WHO: (Primary User: [casuser], Surrogate User: [testuser])
WHAT: ST-1-u_R_SyXJJlENS0fBLwpecNE for https://example.app.edu
ACTION: SERVICE_TICKET_CREATED
APPLICATION: CAS
WHEN: Mon Sep 11 12:55:07 MST 2017
CLIENT IP ADDRESS: 127.0.0.1
SERVER IP ADDRESS: 127.0.0.1
=============================================================
```

Additionally, failure and success events may also communicated via SMS and/or email messages to relevant parties. To learn more about available options, please [see this guide](SMS-Messaging-Configuration.html) or [this guide](Sending-Email-Configuration.html).
