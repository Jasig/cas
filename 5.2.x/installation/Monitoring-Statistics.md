---
layout: default
title: CAS - Monitoring & Statistics
---

# Monitoring / Statistics

The following endpoints are available and secured by CAS:

| URL                               | Description
|-----------------------------------|------------------------------------------
| `/status/dashboard`               | The control panel to CAS server functionality and management.
| `/status`                         | [Monitor CAS status and other underlying components](Configuring-Monitoring.html).
| `/status/sso`                     | Describes if there exists an active SSO session for this request tied to this browser session.
| `/status/swf`                     | Describes the current configured state of CAS webflow in JSON.
| `/status/stats`                   | Visual representation of CAS statistics with graphs and charts, etc.
| `/status/logging`                 | Monitor CAS logs in a streaming fashion and review the audit log.
| `/status/config`                  | Visual representation of application properties and configuration.
| `/status/ssosessions`             | Reports active SSO sessions. Examine attributes, services and log users out.
| `/status/services`                | Reports the collection of [applications registered with CAS](Service-Management.html).
| `/status/trustedDevs`             | Reports on the [registered trusted devices/browsers](Multifactor-TrustedDevice-Authentication.html).
| `/status/authnEvents`             | When enabled, reports on the [events captured by CAS](Configuring-Authentication-Events.html).
| `/status/attrresolution`          | Examine resolution of user attributes via [CAS attribute resolution](../integration/Attribute-Resolution.html).
| `/status/discovery`               | Advertises the CAS server's profile, features and capabilities for auto-configuration of client applications.

The following endpoints are secured and available 
by [Spring Boot actuators](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html):

| URL                               | Description
|-----------------------------------|-------------------------------------------------------------------------------------
| `/status/autoconfig`              | Describes how the CAS application context is auto-configured. 
| `/status/beans`                   | Displays all CAS application context **internal** Spring beans.
| `/status/configprops`             | List of **internal** configuration properties.
| `/status/dump`                    | Produces a thread dump for the running CAS server.
| `/status/env`                     | Produces a collection of all application properties.
| `/status/health`                  | Reports back general health status of the system, produced by various monitors.
| `/status/info`                    | CAS version information and other system traits.
| `/status/metrics`                 | Runtime metrics and stats.
| `/status/mappings`                | Describes how requests are mapped and handled by CAS.
| `/status/shutdown`                | Shut down the application via a `POST`. Disabled by default.
| `/status/restart`                 | Restart the application via a `POST`. Disabled by default.
| `/status/refresh`                 | Refresh the application configuration via a `POST` to let components reload and recognize new values.

Actuator endpoints provided by Spring Boot can also be visually managed and monitored via the [Spring Boot Administration Server](Configuring-Monitoring-Administration.html).

## Security

All urls that are scoped to the `/status` endpoint are modeled after Spring Boot's own actuator endpoints
and by default are considered `sensitive`. By default, no endpoint is enabled or allowed access.

Endpoints may go through multiple levels and layers of security described here:

- All endpoints may be globally considered `sensitive`.
- Spring Boot's actuator endpoints may be individually marked as `sensitive` or `enabled`.
- Similarly, CAS endpoints may be individually marked as `sensitive` or `enabled`.
- In the event that access to an endpoint is allowed, (i.e endpoint is enabled and is not marked as sensitive), CAS will attempt
to control access by enforcing rules via IP address matching, delegating to itself, etc. The `/status` endpoint is always protected by an IP pattern. The other administrative endpoints however can optionally be protected by the CAS server. Failing to secure these endpoints via a CAS instance will have CAS fallback onto the IP range.
    - If you decide to protect other administrative endpoints via CAS itself, you will need to provide
    a reference to the list of authorized users in the CAS configuration. You may also enforce authorization
    rules via [Service-based Access Strategy](Configuring-Service-Access-Strategy.html) features of CAS.

<div class="alert alert-warning"><strong>Reverse Proxies</strong><p>Allowing access to the <code>/status</code> endpoint
via IP address matching needs to be very carefully designed, specially in cases where CAS is deployed behind a proxy
such as Apache. Be sure to test access rules and policies carefully or otherwise devise your own.</p></div>

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#admin-status-endpoints).

### Spring Security

Alternatively, you may design the security of CAS `/status` endpoints to take advantage
of [Spring Security](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-monitoring.html).
After all, that's what `sensitive` is designed to do. Using this model and via CAS settings, you get to define 
the authentication scheme (i.e. `BASIC`) as well
as the protected/ignored paths and pre-defined "master" username/password that is used for authentication.
If the password is left blank, a random password will be generated/printed in the logs by default.
Besides the master credentials, backend authentication support via LDAP and JDBC storage facilities are also available.

Support is enabled by including the following module in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-webapp-config-security</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#securing-endpoints-with-spring-security).

## Monitors

Monitors allow you to watch the internal state of a given CAS component.
See [this guide](Configuring-Monitoring.html) for more info.

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

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#sleuth-distributed-tracing).

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
