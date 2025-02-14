---
layout: default
title: CAS - Monitoring & Statistics
category: Monitoring & Statistics
---

# Monitoring / Statistics

Actuator endpoints used to monitor and diagnose the internal configuration of the CAS server are typically
exposed over the endpoint `/actuator`.

## Spring Boot Endpoints

The following endpoints are secured and available by 
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
| `shutdown`                | Shut down the application via a `POST`. Disabled by default.
| `restart`                 | Restart the application via a `POST`. Disabled by default.
| `refresh`                 | Refresh the application configuration via a `POST` to let components reload and recognize new values.
| `heapdump`                | Returns a GZip compressed hprof heap dump file.
| `jolokia`                 | Exposes JMX beans over HTTP when Jolokia is configured and included in CAS.
| `logfile`                 | Returns the contents of the log file if `logging.file` or `logging.path` properties are set with support for HTTP `Range` header.
| `prometheus`              | Exposes metrics in a format that can be scraped by a Prometheus server.

<div class="alert alert-info"><strong>Exposed Endpoints</strong><p>
Note that by default the only endpoints exposed over the web are <code>info</code>, <code>status</code>, <code>health</code> and <code>configurationMetadata</code>.
Other endpoints need to be explicitly <strong>enabled and then exposed over the web</strong> in CAS settings in order to allow access.
</p></div>

Actuator endpoints provided by Spring Boot can also be visually managed and monitored
 via the [Spring Boot Administration Server](Configuring-Monitoring-Administration.html).
<div class="alert alert-info"><strong>Obtaining Health Info</strong><p>Note that <code>/status</code> endpoint is kept mostly 
as a legacy endpoint. If you wish to obtain health status of each monitor in detail, we recommend the <code>/actuator/health</code> endpoint instead.</p></div>

## CAS Endpoints

The default set of CAS actuator endpoints can be turned in by including the following dependency in the WAR overlay:
                                                           
```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-reports</artifactId>
    <version>${cas.version}</version>
</dependency>
```                                       

Note that certain CAS features present actuator endpoints of their own, and such endpoints only become active
once the feature is turned on and made available to CAS at runtime. For more information, refer to the specific
documentation page for the feature in question to learn more about administrative endpoints, etc.

## Metrics

Navigating to `/actuator/metrics` displays a list of available meter names. You can drill down to view information about a 
particular meter by providing its name as a selector, e.g. `/actuator/metrics/jvm.memory.max`.  The name you use here should match 
the name used in the code, not the name after it has been naming-convention normalized for a monitoring system it is shipped to.

You can also add any number of `tag=KEY:VALUE` query parameters to the end of the URL to dimensionally drill 
down on a meter, e.g. `/actuator/metrics/jvm.memory.max?tag=area:nonheap`

The reported measurements are the sum of the statistics of all meters matching the meter name and any tags that have been applied. 
So in the example above, the returned "Value" statistic is the sum of the maximum memory footprints of "Code Cache", 
"Compressed Class Space", and "Metaspace" areas of the heap. If you just wanted to see the maximum size for the "Metaspace", 
you could add an additional `tag=id:Metaspace`, i.e. `/actuator/metrics/jvm.memory.max?tag=area:nonheap&tag=id:Metaspace`.

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
