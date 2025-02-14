---
layout: default
title: CAS - Loggly Logging Configuration
category: Logs & Audits
---

# Loggly Configuration

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
