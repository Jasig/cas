---
layout: default
title: CAS - Logstash Logging Configuration
category: Logs & Audits
---

# Logstash Logging

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
...
```
