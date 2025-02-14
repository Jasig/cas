---
layout: default
title: CAS - Configuring Service SSO Policy
category: Services
---

# Configuring Service SSO Policy

Single Sign-on participation policies designed on a per-service basis should override the global SSO behavior. Such policies generally are applicable
to participation in single sign-on sessions, creating SSO cookies, etc. 

## Single Sign-on Cookie

CAS adopters may want to allow for a behavior where logging in to a non-SSO-participating application
via CAS either does not create a CAS SSO session and the SSO session it creates is not honored for authenticating subsequently
to an SSO-participating application. This behavior can be defined on a per-service basis.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "...",
  "name" : "...",
  "id" : 1,
  "singleSignOnParticipationPolicy": {
    "@class": "org.apereo.cas.services.DefaultRegisteredServiceSingleSignOnParticipationPolicy",
    "createCookieOnRenewedAuthentication": "TRUE"
  }
}
```         

Acceptable values for `createCookieOnRenewedAuthentication` are `TRUE`, `FALSE` or `UNDEFINED`. 

## Participation Policies

Additional policies can be assigned to each service definition to control participation of an application in an existing single sign-on session.
If conditions hold true, CAS shall honor the existing SSO session and will not challenge the user for credentials. If conditions fail, then
user may be asked for credentials. Such policies can be chained together and executed in order.

### Authentication Date

Honor the existing single sign-on session, if any, if the authentication date is at most `5` seconds old. Otherwise, challenge 
the user for credentials and ignore the existing session.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "...",
  "name" : "...",
  "id" : 1,
  "singleSignOnParticipationPolicy":
    {
      "@class": "org.apereo.cas.services.ChainingRegisteredServiceSingleSignOnParticipationPolicy",
      "policies": [ "java.util.ArrayList",
        [
          {
            "@class": "org.apereo.cas.services.AuthenticationDateRegisteredServiceSingleSignOnParticipationPolicy",
            "timeUnit": "SECONDS",
            "timeValue": 5,
            "order": 0
          }
        ]
      ]
    }
}
```

### Last Used Time

Honor the existing single sign-on session, if any, if the last time an SSO session was used is at most `5` seconds old. Otherwise, challenge the 
user for credentials and ignore the existing session.

The policy calculation here typically includes evaluating the last-used-time of the ticket-granting ticket linked to the SSO session to check whether
the ticket continues to actively issue service tickets, etc. 

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "...",
  "name" : "...",
  "id" : 1,
  "singleSignOnParticipationPolicy":
    {
      "@class": "org.apereo.cas.services.ChainingRegisteredServiceSingleSignOnParticipationPolicy",
      "policies": [ "java.util.ArrayList",
        [
          {
            "@class": "org.apereo.cas.services.LastUsedTimeRegisteredServiceSingleSignOnParticipationPolicy",
            "timeUnit": "SECONDS",
            "timeValue": 5,
            "order": 0
          }
        ]
      ]
    }
}
```

## Custom 

Participation in a single sign-on session can be customized and controlled using custom strategies registered with CAS per the below syntax:

```java
@Bean
public SingleSignOnParticipationStrategyConfigurer customSsoConfigurer() {
    return chain -> chain.addStrategy(...);
}
```

[See this guide](../configuration/Configuration-Management-Extensions.html) to learn more about how to register configurations into the CAS runtime.

