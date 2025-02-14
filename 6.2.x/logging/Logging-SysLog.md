---
layout: default
title: CAS - SysLog Logging Configuration
category: Logs & Audits
---

# SysLog Logging

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
