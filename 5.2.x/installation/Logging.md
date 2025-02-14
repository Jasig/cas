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

## Routing Logs to Sentry

Log data can be automatically routed to and integrated with [Sentry](../integration/Sentry-Integration.html) to track and monitor CAS events and errors.

## Routing Logs to Papertrail

[Papertrail](https://papertrailapp.com) is a cloud-based log management service that provides aggregated logging tools, 
flexible system groups, team-wide access, long-term archives, charts and analytics exports, monitoring webhooks and more.

See [this guide](http://help.papertrailapp.com/kb/configuration/java-log4j-logging/#log4j2) for more info.

```xml
...
<Appenders>
    <Syslog name="Papertrail"
            host="<host>.papertrailapp.com"
            port="XXXXX"
            protocol="TCP" appName="MyApp" mdcId="mdc"
            facility="LOCAL0" enterpriseNumber="18060" newLine="true"
            format="RFC5424" ignoreExceptions="false" exceptionPattern="%throwable{full}">
    </Syslog>
</Appenders>
...
<Loggers>
    <Root level="INFO">
        <AppenderRef ref="Papertrail" />
    </Root>
</Loggers>
```

## Routing Logs to Loggly

[Loggly](https://www.loggly.com) is a cloud-based log management service that makes it easy to access and analyze the mission-critical information within your logs.
Log data can be automatically routed to Loggly via Rsyslog. The advantage of using Rsyslog is that it can send TCP events without blocking your application, can optionally encrypt the data, and even queue data to add robustness to network failure.

See [this guide](https://www.loggly.com/docs/java-log4j-2/) for more info.

```xml
...
<Appenders>
    <Socket name="Loggly" host="localhost" port="514" protocol="UDP">
        <PatternLayout>
        <pattern>${hostName} java %d{yyyy-MM-dd HH:mm:ss,SSS}{GMT} %p %t
            %c %M - %m%n</pattern>
        </PatternLayout>
    </Socket>
</Appenders>
...
<Loggers>
    <Root level="INFO">
        <AppenderRef ref="Loggly" />
    </Root>
</Loggers>
```

## Routing Logs to CloudWatch

Log data can be automatically routed to [AWS CloudWatch](https://aws.amazon.com/cloudwatch/). Support is enabled by including the following module in the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-core-logging-config-cloudwatch</artifactId>
     <version>${cas.version}</version>
</dependency>
```

With the above module, you may then declare a specific appender to communicate with AWS CloudWatch:

```xml
<CloudWatchAppender name="cloudWatch"
                    awsLogGroupName="LogGroupName"
                    awsLogStreamName="LogStreamName"
                    awsLogRegionName="us-west-1"
                    credentialAccessKey="..."
                    credentialSecretKey="..."
                    awsLogStreamFlushPeriodInSeconds="5">
    <PatternLayout>
        <Pattern>%5p | %d{ISO8601}{UTC} | %t | %C | %M:%L | %m %ex %n</Pattern>
    </PatternLayout>
</CloudWatchAppender>
...
<AsyncLogger name="org.apereo" additivity="true" level="debug">
    <appender-ref ref="cloudWatch" />
</AsyncLogger>
```

The AWS credentials for access key, secret key and region, if left undefined, may also be retrieved from
system properties via `AWS_ACCESS_KEY`, `AWS_SECRET_KEY` and `AWS_REGION_NAME`.
The group name as well as the stream name are automatically created by CAS, if they are not already found.

## Routing Logs to Logstash

CAS logging framework has the ability route log messages to a TCP/UDP endpoint.
This configuration assumes that the Logstash server has enabled its [TCP input](https://www.elastic.co/guide/en/logstash/current/plugins-inputs-tcp.html) on port `9500`:

```xml
...
<Appenders>
    <Socket name="socket" host="localhost" connectTimeoutMillis="3000"
            port="9500" protocol="TCP" ignoreExceptions="false">
      <SerializedLayout />
    </Socket>
</Appenders>
...
<AsyncLogger name="org.apereo" additivity="true" level="debug">
    <appender-ref ref="cas" />
    <appender-ref ref="socket" />
</AsyncLogger>

```

## Routing Logs to SysLog

CAS logging framework does have the ability to route messages to an external
syslog instance. To configure this,
you first to configure the `SysLogAppender` and then specify which
messages needs to be routed over to this instance:

```xml
...
<Appenders>
    <Syslog name="SYSLOG" format="RFC5424" host="localhost" port="8514"
            protocol="TCP" appName="MyApp" includeMDC="true" mdcId="mdc"
            facility="LOCAL0" enterpriseNumber="18060" newLine="true"
            messageId="Audit" id="App"/>
</Appenders>
...
<AsyncLogger name="org.apereo" additivity="true" level="debug">
    <appender-ref ref="cas" />
    <appender-ref ref="SYSLOG" />
</AsyncLogger>

```

You can also configure the remote destination output over
SSL and specify the related keystore configuration:

```xml
...

<Appenders>
    <TLSSyslog name="bsd" host="localhost" port="6514">
      <SSL>
        <KeyStore location="log4j2-keystore.jks" password="changeme"/>
        <TrustStore location="truststore.jks" password="changeme"/>
      </SSL>
    </TLSSyslog>
</Appenders>

...

```

## Mapped Diagnostic Context

To uniquely stamp each request, CAS puts contextual
information into the `MDC`, the abbreviation of Mapped Diagnostic Context. This effectively
translates to a number of special variables available to the logging context that
may convey additional information about the nature of the request or the authentication event.

| Variable                                     | Description
|-----------------------------------|-------------------------------------
| `remoteAddress`                     | Remote address of the HTTP request.
| `remoteUser`                        | Remote user of the HTTP request.
| `serverName`                        | Server name of the HTTP request.
| `serverPort`                        | Server port of the HTTP request.
| `locale`                            | Locale of the HTTP request.
| `contentType`                       | Content type of the HTTP request.
| `contextPath`                       | Context path of the HTTP request.
| `localAddress`                      | Local address of the HTTP request.
| `localPort`                         | Local port of the HTTP request.
| `remotePort`                        | Remote port of the HTTP request.
| `pathInfo`                          | Path information of the HTTP request.
| `protocol`                          | Protocol of the HTTP request.
| `authType`                          | Authentication type of the HTTP request.
| `method`                            | Method of the HTTP request.
| `queryString`                       | Query string of the HTTP request.
| `requestUri`                        | Request URI of the HTTP request.
| `scheme`                            | Scheme of the HTTP request.
| `timezone`                          | Timezone of the HTTP request.
| `principal`                         | CAS authenticated principal id.

Additionally, all available request attributes, headers, and parameters are exposed as variables.

The above variables may be used in logging patterns:

- Use `%X` by itself to include all variables.
- Use `%X{key}` to include the specified variable.

```xml
<Console name="console" target="SYSTEM_OUT">
    <PatternLayout pattern="%X{locale} %d %p [%c] - &lt;%m&gt;%n"/>
</Console>
```
