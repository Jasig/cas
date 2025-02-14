---
layout: default
title: CAS - Logging Configuration
---

# Logging

CAS provides a logging facility that logs important informational events like authentication success and
failure; it can be customized to produce additional information for troubleshooting. CAS uses the Slf4J
Logging framework as a facade for the [Log4J engine](http://logging.apache.org) by default.

The default log4j configuration file is located in `src/main/resources/log4j2.xml`.
By default logging is set to `INFO` for all functionality related to `org.apereo.cas` code.
For debugging and diagnostic purposes you may want to set these levels to  `DEBUG`.

<div class="alert alert-warning"><strong>Production</strong><p>You should always run everything under
<code>WARN</code>. In production
warnings and errors are things you care about. Everything else is just diagnostics. Only
turn up <code>DEBUG</code> or <code>INFO</code> if you need to research a particular issue.</p></div>

## Configuration

It is often time helpful to externalize the `log4j2.xml` file to a system path to preserve settings between upgrades.
The location of `log4j2.xml` file by default is on the runtime classpath and can be controlled
via the CAS properties. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#logging).

<div class="alert alert-info"><strong>Monitoring Logs</strong><p>To review log settings and output,
 you may also use the <a href="Monitoring-Statistics.html">CAS administration panels.</a></p></div>

### Log Levels

While log levels can directly be massaged via the native `log4j2.xml` syntax, they may also be modified
using the usual CAS properties. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#logging).

### Refresh Interval

The `log4j2.xml` itself controls the refresh interval of the logging configuration. Log4j has the ability
to automatically detect changes to the configuration file and reconfigure itself. If the `monitorInterval`
attribute is specified on the configuration element and is set to a non-zero value then the file will be
checked the next time a log event is evaluated and/or logged and the `monitorInterval` has elapsed since
the last check. This will allow you to adjust the log levels and configuration without restarting the
server environment.

```xml
<!-- Specify the refresh internal in seconds. -->
<Configuration monitorInterval="15" ...>
        ...
</Configuration>
```

### Log Patterns

By default most appenders that are provided via the `log4j2.xml` file use
pattern-based layouts to format log messages. The following alternative layouts may also be used:

| Layout                        | Description
|-------------------------------|------------------------------------------------------------------------
| `CsvParameterLayout`          | Converts an event's parameters into a CSV record, ignoring the message.
| `GelfLayout`                  | Lays out events in the Graylog Extended Log Format (GELF).
| `HTMLLayout`                  | Generates an HTML page and adds each LogEvent to a row in a table
| `JSONLayout`                  | Creates log events in well-formed or fragmented JSON.
| `PatternLayout`               | Formats the log even based on a conversion pattern.
| `RFC5424Layout`               | Formats log events in accordance with [RFC 5424](http://tools.ietf.org/html/rfc5424), the enhanced Syslog specification.
| `SerializedLayout`            | Log events are transformed into byte arrays useful in JMS or socket connections.
| `SyslogLayout`                | Formats log events as BSD Syslog records.
| `XMLLayout`                   | Creates log events in well-formed or fragmented XML.
| `YamlLayout`                  | Creates log events in YAML.

To learn more about nuances and configuration settings for each, please refer to the [official Log4J guides](http://logging.apache.org).

## Log File Rotation

The default configuration specifies triggering policies for rolling over logs, at startup, size or at specific times. These policies apply to `RollingFile` appenders.

For example, the following XML fragment defines policies that rollover the log when the JVM starts, when the log size reaches `10` megabytes, and when the current date no longer matches the log’s start date.

```xml
<RollingFile name="file" fileName="${baseDir}/cas.log" append="true"
                    filePattern="${baseDir}/cas-%d{yyyy-MM-dd-HH}-%i.log">
    ...
    <Policies>
        <OnStartupTriggeringPolicy />
        <SizeBasedTriggeringPolicy size="10 MB"/>
        <TimeBasedTriggeringPolicy />
    </Policies>
    ...
</RollingFile>
```

The triggering policies determines **if** a rollover should be performed and rollover strategy can also be design to indicate **how** that should be done. If no strategy is configured, the default will be used.

To find more a comprehensive documentation, please [review the guides here](http://logging.apache.org).

### Rollover Strategy

Customized rollover strategies provide a delete action that gives users more control over what files are deleted at rollover time than what was possible with the DefaultRolloverStrategy max attribute. The delete action lets users configure one or more conditions that select the files to delete relative to a base directory.

For example, the following appender at rollover time deletes all files under the base directory that match the `*/*.log` glob and are `7` days old or older.

```xml
<RollingFile name="file" fileName="${baseDir}/cas.log" append="true"
             filePattern="${baseDir}/cas-%d{yyyy-MM-dd-HH}-%i.log">
    ...
    <DefaultRolloverStrategy max="5">
        <Delete basePath="${baseDir}" maxDepth="2">
            <IfFileName glob="*/*.log" />
            <IfLastModified age="7d" />
        </Delete>
    </DefaultRolloverStrategy>
    ...
</RollingFile>
```

To find more a comprehensive documentation, please [review the guides here](http://logging.apache.org).

## Log Data Sanitation

For security purposes, CAS by default will attempt to remove TGT and PGT ids from all log data.
This will of course include messages that are routed to a log destination by the logging framework as
 well as all audit messages. A sample follows below:

```bash
WHO: audit:unknown
WHAT: TGT-******************123456-cas01.example.org
ACTION: TICKET_GRANTING_TICKET_DESTROYED
APPLICATION: CAS
WHEN: Sat Jul 12 04:10:35 PDT 2014
CLIENT IP ADDRESS: ...
SERVER IP ADDRESS: ...
```

Certain number of characters are left at the trailing end of the ticket id to assist with
troubleshooting and diagnostics.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#logging).