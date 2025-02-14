---
layout: default
title: CAS - Monitoring & Statistics
category: Monitoring & Statistics
---

# Monitoring / Statistics

Actuator endpoints used to monitor and diagnose the internal configuration of the CAS server are typically
exposed over the endpoint `/actuator`. The following endpoints are secured and available by 
[Spring Boot actuators](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html):

| Endpoint                  | Description
|---------------------------|-------------------------------------------------------------------------------------
| `autoconfig`              | Describes how the CAS application context is auto-configured. 
| `beans`                   | Displays all CAS application context **internal** Spring beans.
| `conditions`              | Shows the conditions that were evaluated on configuration and auto-configuration classes and the reasons why they did or did not match.
| `configprops`             | List of **internal** configuration properties.
| `threaddump`              | Produces a thread dump for the running CAS server.
| `env`                     | Produces a collection of all application properties.
| `health`                  | Reports back general health status of the system, produced by various monitors.
| `info`                    | CAS version information and other system traits.
| `metrics`                 | Runtime metrics and stats.
| `httptrace`               | Displays HTTP trace information (by default, the last 100 HTTP request-response exchanges).
| `mappings`                | Describes how requests are mapped and handled by CAS.
| `scheduledtasks`          | Displays the scheduled tasks in CAS.
| `mappings`                | Describes how requests are mapped and handled by CAS.
| `shutdown`                | Shut down the application via a `POST`. Disabled by default.
| `restart`                 | Restart the application via a `POST`. Disabled by default.
| `refresh`                 | Refresh the application configuration via a `POST` to let components reload and recognize new values.
| `heapdump`                | Returns a GZip compressed hprof heap dump file.
| `jolokia`                 | Exposes JMX beans over HTTP when Jolokia is configured and included in CAS.
| `logfile`                 | Returns the contents of the log file if `logging.file` or `logging.path` properties are set with support for HTTP `Range` header.
| `prometheus`              | Exposes metrics in a format that can be scraped by a Prometheus server.

 The following endpoints are provided by CAS:
 
| Endpoint                 | Description
|--------------------------|-------------------------------------------------------------------------------------
| `springWebflow`          | Provides a JSON representation of the CAS authentication webflows.
| `events`                 | Provides a JSON representation of all CAS recorded events.
| `auditLog`               | Provides a JSON representation of all the audit log.
| `discoveryProfile`       | Provides a JSON representation of the [CAS configuration and capabilities](../configuration/Configuration-Discovery.html).
| `registeredServices`     | Provides a JSON representation of the [CAS service registry](../services/Service-Management.html).
| `exportRegisteredServices`    | Provides a ZIP-file representation of the [CAS service registry](../services/Service-Management.html).
| `configurationMetadata`       | Exposes [CAS configuration metadata](../configuration/Configuration-Metadata-Repository.html) that can be used to query settings.
| `statistics`                  | Exposes statistics data on tickets, memory, server availability and uptime, etc.
| `ssoSessions`                 | Review the current single sign-on sessions establishes with CAS and manage each session remotely.
| `sso`                         | Indicate the current status of the single signon session tied to the browser session and the SSO cookie.
| `resolveAttributes/{name}`    | Invoke the CAS [attribute resolution](../integration/Attribute-Resolution.html) engine to locate attributes for `{name}`.
| `releaseAttributes`           | Invoke the CAS [attribute release](../integration/Attribute-Release.html) engine to release attributes to an application.
| `multifactorTrustedDevices`   | Expose devices currently [registered and trusted](../mfa/Multifactor-TrustedDevice-Authentication.html) by the CAS multifactor authentication engine.
| `attributeConsent`            | Manage and control [attribute consent decisions](../integration/Attribute-Release-Consent.html).
| `gauthCredentialRepository`   | Manage and control [Google Authenticator account records](../mfa/GoogleAuthenticator-Authentication.html).
| `yubikeyAccountRepository`    | Manage and control [Google Authenticator account records](../mfa/YubiKey-Authentication.html).
| `oauthTokens`                 | Manage and control [OAuth2 access tokens](../installation/OAuth-OpenId-Authentication.html).
| `consentReview`               | Manage and control [Consent decisions](../integration/Attribute-Release-Consent.html).

<div class="alert alert-info"><strong>Exposed Endpoints</strong><p>
Note that by default the only endpoints exposed over the web are <code>info</code>, <code>status</code>, <code>health</code> and <code>configurationMetadata</code>.
Other endpoints need to be explicitly enabled and then exposed over the web in CAS settings in order to allow access.
</p></div>

Actuator endpoints provided by Spring Boot can also be visually managed and monitored
 via the [Spring Boot Administration Server](Configuring-Monitoring-Administration.html).
<div class="alert alert-info"><strong>Obtaining Health Info</strong><p>Note that <code>/status</code> endpoint is kept mostly 
as a legacy endpoint. If you wish to obtain health status of each monitor in detail, we recommend the <code>/actuator/health</code> endpoint instead.</p></div>
 
### Registered Services Endpoint

The endpoint can also accept a mime-type of `application/vnd.cas.services+yaml` to produce YAML output.

### Attribute Release Endpoint

Supported parameters are the following:

| Query Parameter           | Description
|---------------------------|--------------------------------------------
| `username`                | The username to use for authentication.
| `password`                | The password to use for authentication.
| `service`                 | Service to which attributes should be released.

### Spring Webflow Endpoint

The endpoint can accept a `flowId` parameter as part of a `GET` operation to only present the flow body of the requested flow id.

### Single SignOn Sessions Endpoint

A `GET` operation produces a list of current SSO sessions that are filtered by a provided `type` parameter with values `ALL`, `PROXIED` or `DIRECT`.

A `DELETE` operation without specifying a ticket id will attempt to destroy all SSO sessions. Specifying a ticket-granting ticket identifier 
in the URL as a placeholder/selector will attempt to destroy the session controlled by that ticket. (i.e. `ssoSessions/{ticket}`)

### Multifactor Trusted Devices

A `GET` operation produces a list of all trusted devices. Specifying a username in the URL
as the placeholder/selector will fetch devices registered for that user (i.e. `multifactorTrustedDevices/{/{username}`).

A `DELETE` operation with a device key  id will attempt to remove the trusted device (i.e. `multifactorTrustedDevices/{/{id}`).

### Attribute Release Consent

A `GET` operation produces a list of all consent decisions.
A `DELETE` operation with a record key id will attempt to remove and revoke the registered device (i.e. `attributeConsent/{principal}/{id}`).

### Google Authenticator Accounts

A `GET` operation produces a list of all account records.
A `DELETE` operation will delete all account records.

A `GET` operation produces with a parameter selector of `/{username}` will list the record assigned to the user.
A `DELETE` operation produces with a parameter selector of `/{username}` will remove the record assigned to the user.

### YubiKey Accounts

A `GET` operation produces a list of all account records.
A `DELETE` operation will delete all account records.

A `GET` operation produces with a parameter selector of `/{username}` will list the record assigned to the user.
A `DELETE` operation produces with a parameter selector of `/{username}` will remove the record assigned to the user.

### OAuth Tokens

A `GET` operation produces a list of all access/refresh tokens.
A `DELETE` operation will delete the provided access/refresh token provided in form of a parameter selector. (i.e. `/{token}`)
A `GET` operation produces with a parameter selector of `/{token}` will list the details of the fetched access/refresh token.

### Metrics

Navigating to `/actuator/metrics` displays a list of available meter names. You can drill down to view information about a 
particular meter by providing its name as a selector, e.g. `/actuator/metrics/jvm.memory.max`.  The name you use here should match 
the name used in the code, not the name after it has been naming-convention normalized for a monitoring system it is shipped to.

You can also add any number of `tag=KEY:VALUE` query parameters to the end of the URL to dimensionally drill 
down on a meter, e.g. `/actuator/metrics/jvm.memory.max?tag=area:nonheap`

The reported measurements are the sum of the statistics of all meters matching the meter name and any tags that have been applied. 
So in the example above, the returned "Value" statistic is the sum of the maximum memory footprints of "Code Cache", 
"Compressed Class Space", and "Metaspace" areas of the heap. If you just wanted to see the maximum size for the "Metaspace", 
you could add an additional `tag=id:Metaspace`, i.e. `/actuator/metrics/jvm.memory.max?tag=area:nonheap&tag=id:Metaspace`.

<div class="alert alert-info"><strong>Use <code>/actuator/health</code> instead of <code>/status</code> </strong><p>Note that <code>/status</code> endpoint is kept for legacy reason. 
It is advised to use <code>/actuator/health</code> instead of <code>/status</code> for the purpose of general health status monitoring</p></div>

## Security

Once endpoints are enabled and exposed, the security of all provided endpoints is handled 
by [Spring Security](https://spring.io/projects/spring-security). Protection and access to each endpoint
is controlled via CAS settings individually such that you may decide a specific security level and method of authentication for each endpoint independently.

If CAS is configured to *NOT* enforce endpoint security rules, then all endpoints are considered sensitive and require authentication, typically handled
via basic authentication with master credentials defined in CAS settings. 

If CAS is configured to enforce endpoint security rules, then each endpoint may be tagged with a specific security rule allowing access via authorized IP addresses,
basic credentials, roles and attributes, etc. 

Authentication credentials are typically controlled via CAS settings. For basic authentication, the default username is `casuser`. The password 
may be automatically generated at startup and displayed in CAS logs if it is left undefined in CAS settings. Additional sources may also be defined
that would authenticate the request via JAAS, LDAP, JDBC, etc.

Depending on method of access and the `content-type` that is negotiated between the caller and CAS, (i.e. web-based vs. command-line access), 
credentials may be supplied in headers via `curl` and family or they may be entered into a web-based login form.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#actuator-management-endpoints).

### Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following
levels:

```xml
...
<AsyncLogger name="org.pac4j" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
<AsyncLogger name="org.springframework.security" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```


Monitors allow you to watch the internal state of a given CAS component. ````See [this guide](Configuring-Monitoring.html) for more info.

## Distributed Tracing

Support for distributed tracing of requests is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-sleuth</artifactId>
     <version>${cas.version}</version>
</dependency>
```

![image](https://cloud.githubusercontent.com/assets/1205228/24955152/8798ad9c-1f97-11e7-8b9d-fccc3c306c42.png)

For most users [Sleuth](https://cloud.spring.io/spring-cloud-sleuth/) should be invisible, and all
interactions with external systems should be instrumented automatically.

Trace data is captured automatically and passed along to [Zipkin](https://github.com/openzipkin/zipkin), which helps 
gather timing data needed to troubleshoot latency problems.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#sleuth-distributed-tracing).

### Troubleshooting

To enable additional logging, modify the logging configuration file to add the following:

```xml
 <AsyncLogger name="org.springframework.cloud" level="debug" additivity="false">
    <AppenderRef ref="casConsole"/>
    <AppenderRef ref="casFile"/>
</AsyncLogger>
```

## Metrics

Metrics allow to gain insight into the running CAS software, and provide ways to measure the behavior of critical components. 
See [this guide](Configuring-Metrics.html) for more info.
