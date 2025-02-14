---
layout: default
title: CAS - Audit Configuration
---

# Audits

CAS uses the [Inspektr framework](https://github.com/apereo/inspektr) for auditing purposes
and statistics. The Inspektr project allows for non-intrusive auditing and logging of the
coarse-grained execution paths e.g. Spring-managed beans method executions by using annotations
and Spring-managed `@Aspect`-style aspects.


CAS server auto-configures all the relevant Inspektr components.
All the available configuration
options that are injected to Inspektr classes are available to
deployers via relevant CAS properties.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#audits).


## Sentry-based Audits

Audit log data can be automatically routed to and integrated with [Sentry](../integration/Sentry-Integration.html) to track and monitor CAS events and errors.

## File-based Audits

File-based audit logs appear in a `cas_audit.log` file defined in the [Logging](Logging.html) configuration.
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#audits).

### Sample Log Output

```bash
WHO: org.apereo.cas.support.oauth.authentication.principal.OAuthCredentials@6cd7c975
WHAT: supplied credentials: org.apereo.cas.support.oauth.authentication.principal.OAuthCredentials@6cd7c975
ACTION: AUTHENTICATION_SUCCESS
APPLICATION: CAS
WHEN: Mon Aug 26 12:35:59 IST 2013
CLIENT IP ADDRESS: 172.16.5.181
SERVER IP ADDRESS: 192.168.200.22

WHO: org.apereo.cas.support.oauth.authentication.principal.OAuthCredentials@6cd7c975
WHAT: TGT-9-qj2jZKQUmu1gQvXNf7tXQOJPOtROvOuvYAxybhZiVrdZ6pCUwW-cas01.example.org
ACTION: TICKET_GRANTING_TICKET_CREATED
APPLICATION: CAS
WHEN: Mon Aug 26 12:35:59 IST 2013
CLIENT IP ADDRESS: 172.16.5.181
SERVER IP ADDRESS: 192.168.200.22
```

## Database Audits

If you intend to use a database for auditing functionality, enable the following module in your configuration:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-audit-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To learn how to configure database drivers, please [review this guide](JDBC-Drivers.html).
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#database-audits).

## MongoDb Audits

If you intend to use a MongoDb database for auditing functionality, enable the following module in your configuration:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-audit-mongo</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#mongodb-audits).

## Audit Events

The following events are tracked and recorded in the audit log:

| Event                                 | Action          
|---------------------------------------|--------------------------------------
| `TICKET_GRANTING_TICKET`              | `CREATED`, `NOT_CREATED`, `DESTROYED`
| `PROXY_GRANTING_TICKET`               | `CREATED`, `NOT_CREATED`, `DESTROYED`
| `SERVICE_TICKET`                      | `CREATED`, `NOT_CREATED`
| `PROXY_TICKET`                        | `CREATED`, `NOT_CREATED`
| `AUTHENTICATION`                      | `SUCCESS`, `FAILED`
| `AUTHENTICATION_EVENT`                | `TRIGGERED`
| `EVALUATE_RISKY_AUTHENTICATION`       | N/A
| `MITIGATE_RISKY_AUTHENTICATION`       | N/A
| `SAVE_SERVICE`                        | `SUCCESS`, `FAILURE`
| `SAVE_CONSENT`                        | `SUCCESS`, `FAILURE`
| `CHANGE_PASSWORD`                     | `SUCCESS`, `FAILURE`
| `DELETE_SERVICE`                      | `SUCCESS`, `FAILURE`
