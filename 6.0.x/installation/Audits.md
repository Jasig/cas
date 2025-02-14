---
layout: default
title: CAS - Audit Configuration
category: Logs & Audits
---

# Audits

CAS uses the [Inspektr framework](https://github.com/apereo/inspektr) for auditing purposes
and statistics. The Inspektr project allows for non-intrusive auditing and logging of the
coarse-grained execution paths e.g. Spring-managed beans method executions by using annotations
and Spring-managed `@Aspect`-style aspects.

CAS server auto-configures all the relevant Inspektr components.   All the available configuration options that are injected to Inspektr classes are available to deployers via relevant CAS properties. Note that the audit record management functionality of CAS supports handling multiple audit record destinations at the same time. In other words, you may choose to route audit records to both a database and a REST endpoint as well as any number of logger-based destinations all at the same time.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#audits).

## File-based Audits

File-based audit logs appear in a `cas_audit.log` file defined in the [Logging](../logging/Logging.html) configuration.
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#audits).

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
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#database-audits).

## MongoDb Audits

If you intend to use a MongoDb database for auditing functionality, enable the following module in your configuration:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-audit-mongo</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#mongodb-audits).

## CouchDb Audits

If you intend to use a CouchDb database for auditing functionality, enable the following module in your configuration:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-audit-couchdb</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#couchdb-audits).

## Couchbase Audits

If you intend to use a Couchbase database for auditing functionality, enable the following module in your configuration:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-audit-couchbase</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#couchbase-audits).

## REST Audits

Audit events may also be `POST`ed to an endpoint of your choosing. To activate this feature, enable the following module in your configuration:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-audit-rest</artifactId>
    <version>${cas.version}</version>
</dependency>
```

The body of the HTTP request is a JSON representation of the audit record. 
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#rest-audits).

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
| `SAML2_RESPONSE`                      | `CREATED`, `FAILED`
| `SAML2_REQUEST`                       | `CREATED`, `FAILED`
| `OAUTH2_USER_PROFILE_DATA`            | `CREATED`, `FAILED`
| `OAUTH2_ACCESS_TOKEN_REQUEST`         | `CREATED`, `FAILED`
| `OAUTH2_ACCESS_TOKEN_RESPONSE`        | `CREATED`, `FAILED`
| `REST_API_TICKET_GRANTING_TICKET`     | `CREATED`, `FAILED`
| `REST_API_SERVICE_TICKET`             | `CREATED`, `FAILED`
| `SERVICE_ACCESS_ENFORCEMENT`          | `TRIGGERED`
| `DELEGATED_CLIENT`                    | `SUCCESS`, `FAILURE`
| `SURROGATE_AUTHENTICATION_ELIGIBILITY_VERIFICATION`          | `TRIGGERED`
