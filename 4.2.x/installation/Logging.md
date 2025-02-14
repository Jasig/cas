---
layout: default
title: CAS - Logging Configuration
---


# Logging
CAS provides a logging facility that logs important informational events like authentication success and
failure; it can be customized to produce additional information for troubleshooting. CAS uses the Slf4J
Logging framework as a facade for the [Log4J engine](http://logging.apache.org‎) by default.

The log4j configuration file is located in `WEB-INF/classes/log4j2.xml`.
By default logging is set to `INFO` for all functionality related to `org.jasig.cas` code and `WARN` for
messages related to Spring framework, etc. For debugging and diagnostic purposes you may want to set
these levels to  `DEBUG`.

```xml
...

<AsyncLogger name="org.jasig" level="info" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>

<AsyncLogger name="org.springframework" level="warn" />
...
```

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>When in production though,
you probably want to run them both as `WARN`.</p></div>

## Configuration
It is often helpful to externalize `log4j2.xml` to a system path to preserve settings between upgrades.
The location of `log4j2.xml` file by default is on the runtime classpath. 
These may be overridden via the following system property passed to the container:

```bash
-Dlog4j.configurationFile=/etc/cas/log4j2.xml
```

The `log4j2.xml` file by default at `WEB-INF/classes` provides the following `appender` elements that
decide where and how messages from components should be displayed. Two are provided by default that
output messages to the system console and a `cas.log` file:

### Multiple Logger Bindings
CAS by default attempts to scan the runtime application context looking for suitable logger frameworks. 
By default, the framework that is chosen is Log4j. If there are multiple logging frameworks found 
on the application classpath at runtime, you can instruct CAS to specifically select Log4j as the logging framework
via the following property passed to the JVM runtime instance:

```bash
-DloggerFactory="org.apache.logging.slf4j.Log4jLoggerFactory"
```

### Alternative Loggers
If you wish to use an alternative logging framework other than Log4j, you will need to exclude
all `log4j` JAR artifacts and the `cas-server-core-logging` module from your configuration. Ensure
an alternative framework such as Logback is provided instead to the application runtime and the necessary
configuration is available per the framework. 

### Refresh Interval
The `log4j2.xml` itself controls the refresh interval of the logging configuration. Log4j has the ability
to automatically detect changes to the configuration file and reconfigure itself. If the `monitorInterval`
attribute is specified on the configuration element and is set to a non-zero value then the file will be
checked the next time a log event is evaluated and/or logged and the `monitorInterval` has elapsed since
the last check. This will allow you to adjust the log levels and configuration without restarting the
server environment.

```xml
<!-- Specify the refresh internal in seconds. -->
<Configuration monitorInterval="60">
    <Appenders>
        ...
```

### Appenders
```xml
<Console name="console" target="SYSTEM_OUT">
    <PatternLayout pattern="%d %p [%c] - &lt;%m&gt;%n"/>
</Console>
<RollingFile name="file" fileName="cas.log" append="true"
             filePattern="cas-%d{yyyy-MM-dd-HH}-%i.log">
    <PatternLayout pattern="%d %p [%c] - %m%n"/>
    <Policies>
        <OnStartupTriggeringPolicy />
        <SizeBasedTriggeringPolicy size="10 MB"/>
        <TimeBasedTriggeringPolicy />
    </Policies>
</RollingFile>
```


### AsyncLoggers
Additional AsyncLoggers are available to specify the logging level for component categories.

```xml
<AsyncLogger name="org.jasig" level="info" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
<AsyncLogger name="org.springframework" level="warn" />
<AsyncLogger name="org.springframework.webflow" level="warn" />
<AsyncLogger name="org.springframework.web" level="warn" />
<AsyncLogger name="org.springframework.security" level="warn" />

<AsyncLogger name="org.jasig.cas.web.flow" level="info" additivity="true">
    <AppenderRef ref="file"/>
</AsyncLogger>
<AsyncLogger name="org.jasig.inspektr.audit.support" level="info">
    <AppenderRef ref="file"/>
</AsyncLogger>
<Root level="error">
    <AppenderRef ref="console"/>
</Root>
```

If you wish enable another package for logging, you can simply add another `AsyncLogger`
element to the configuration. Here is an example:

```xml
<AsyncLogger name="org.ldaptive" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
```

## Log Data Sanitation
For security purposes, CAS by default will attempt to remove TGT and PGT ids from all log data.
This will of course include messages that are routed to a log destination by the logging framework as
 well as all audit messages. A sample follows below:

```bash
=============================================================
WHO: audit:unknown
WHAT: TGT-****************************************************123456-cas01.example.org
ACTION: TICKET_GRANTING_TICKET_DESTROYED
APPLICATION: CAS
WHEN: Sat Jul 12 04:10:35 PDT 2014
CLIENT IP ADDRESS: ...
SERVER IP ADDRESS: ...
=============================================================
```

Certain number of characters are left at the trailing end of the ticket id to assist with
troubleshooting and diagnostics. This is achieved by providing a specific binding for the SLF4j configuration.

## AsyncLoggers Shutdown with Tomcat

Log4j automatically inserts itself into the runtime application context in a Servlet 3 environment (i.e. Tomcat 8.x) and will clean up 
the logging context once the container is instructed to shut down. However, Tomcat ignores all JAR files named `log4j*.jar`, which prevents 
this feature from working. You may need to change the `catalina.properties` and remove `log4j*.jar` from the `jarsToSkip` property. 
You may need to do something similar on other containers if they skip scanning Log4j JAR files.

Failure to do so will stop Tomcat to gracefully shut down and causes logger context threads to hang. 
