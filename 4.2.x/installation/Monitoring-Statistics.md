---
layout: default
title: CAS - Monitoring & Statistics
---

# Monitoring
The CAS server exposes a `/status` endpoint that may be used to inquire about the health and general state of the software. 
Access to the endpoint is secured by pac4j at `src/main/webapp/WEB-INF/spring-configuration/securityContext.xml`:

```xml
<mvc:interceptor>
    <mvc:mapping path="/status/**" />
    <mvc:mapping path="/statistics/**" />
    <bean class="org.pac4j.springframework.web.RequiresAuthenticationInterceptor">
        <constructor-arg name="config" ref="config" />
        <constructor-arg name="clientName" value="IpClient" />
    </bean>
</mvc:interceptor>
```

Access is granted the following settings in `cas.properties` file:

```bash
# security configuration based on IP address to access the /status and /statistics pages
# cas.securityContext.adminpages.ip=127\.0\.0\.1|0:0:0:0:0:0:0:1
```


## Sample Output

```bash
Health: OK

    1.MemoryMonitor: OK - 322.13MB free, 495.09MB total.
```

The list of configured monitors are all defined in `deployerConfigContext.xml` file:

```xml
<util:list id="monitorsList">
    <ref bean="memoryMonitor" />
    <ref bean="sessionMonitor" />
</util:list>
```

The following optional monitors are also available:

- `MemcachedMonitor`

```xml

<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-integration-memcached-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>

...

<util:list id="monitorsList">
    <ref bean="memcachedMonitor" />
</util:list>
```


The following settings are available:

```properties
# cache.monitor.warn.free.threshold=10
# cache.monitor.eviction.threshold=0
```

- `EhcacheMonitor`

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-integration-ehcache-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>

...

<util:list id="monitorsList">
    <ref bean="ehcacheMonitor" />
</util:list>
<alias name="ticketGrantingTicketsCache" alias="ehcacheMonitorCache" />
```

The following settings are available:

```properties
# cache.monitor.warn.free.threshold=10
# cache.monitor.eviction.threshold=0
```

- `DataSourceMonitor`

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-jdbc-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>

...
<bean id="pooledConnectionFactoryMonitorExecutorService"
    class="org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean"
    p:corePoolSize="1"
    p:maxPoolSize="1"
    p:keepAliveSeconds="1" />
          
<util:list id="monitorsList">
    <ref bean="dataSourceMonitor" />
</util:list>

<alias name="myDataSource" alias="monitorDataSource" />
```

- `PooledConnectionFactoryMonitor`

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-ldap-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>

...

<util:list id="monitorsList">
    <ref bean="pooledLdapConnectionFactoryMonitor" />
</util:list>

<ldaptive:pooled-connection-factory
        id="pooledConnectionFactoryMonitorConnectionFactory"
        ldapUrl="${ldap.url}"
        blockWaitTime="${ldap.pool.blockWaitTime}"
        failFastInitialize="true"
        connectTimeout="${ldap.connectTimeout}"
        useStartTLS="${ldap.useStartTLS}"
        validateOnCheckOut="${ldap.pool.validateOnCheckout}"
        validatePeriodically="${ldap.pool.validatePeriodically}"
        validatePeriod="${ldap.pool.validatePeriod}"
        idleTime="${ldap.pool.idleTime}"
        maxPoolSize="${ldap.pool.maxSize}"
        minPoolSize="${ldap.pool.minSize}"
        useSSL="${ldap.use.ssl:false}"
        prunePeriod="${ldap.pool.prunePeriod}"
        provider="org.ldaptive.provider.unboundid.UnboundIDProvider"
/>

<bean id="pooledConnectionFactoryMonitorValidator" class="org.ldaptive.pool.SearchValidator" />
```

## Internal Configuration Report

CAS also provides a `/status/config` endpoint that produces a report of the runtime CAS configuration, which includes 
settings defined in the `cas.properties` file. The output of this endpoint is a JSON representation of the 
runtime that is rendered into a modest visualization.

# Statistics
Furthermore, the CAS web application has the ability to present statistical data about the runtime environment as well as ticket registry's performance.

The CAS server exposes a `/statistics` endpoint that may be used to inquire about the runtime state of the software. Access to the endpoint is secured by pac4j at `src/main/webapp/WEB-INF/spring-configuration/securityContext.xml` like for the /status page.


## Performance Statistics
CAS also uses the [Dropwizard Metrics framework](https://dropwizard.github.io/metrics/), that provides set of utilities for calculating and displaying performance statistics.

### Configuration
The metrics configuration is controlled via the `/src/main/webapp/WEB-INF/spring-configuration/metricsContext.xml` file. The configuration will output all performance-related data and metrics to the logging framework. The reporting interval can be configured via the `cas.properties` file:

```bash
# Define how often should metric data be reported. Default is 30 seconds.
# metrics.refresh.interval=30s
```

Various metrics can also be reported via JMX. Metrics are exposes via JMX MBeans.
Supported metrics include:

- Run count and elapsed times for all supported garbage collectors
- Memory usage for all memory pools, including off-heap memory
- Breakdown of thread states, including deadlocks
- File descriptor usage
- ...

### Loggers
All performance data and metrics are routed to a log file via the Log4j configuration:

```xml
...
<RollingFile name="perfFileAppender" fileName="perfStats.log" append="true"
             filePattern="perfStats-%d{yyyy-MM-dd-HH}-%i.log">
    <PatternLayout pattern="%m%n"/>
    <Policies>
        <OnStartupTriggeringPolicy />
        <SizeBasedTriggeringPolicy size="10 MB"/>
        <TimeBasedTriggeringPolicy />
    </Policies>
</RollingFile>

...

<Logger name="perfStatsLogger" level="info" additivity="false">
    <AppenderRef ref="perfFileAppender"/>
</Logger>

```


### Sample Output
```bash
type=GAUGE, name=jvm.gc.Copy.count, value=22
type=GAUGE, name=jvm.gc.Copy.time, value=466
type=GAUGE, name=jvm.gc.MarkSweepCompact.count, value=3
type=GAUGE, name=jvm.gc.MarkSweepCompact.time, value=414
type=GAUGE, name=jvm.memory.heap.committed, value=259653632
type=GAUGE, name=jvm.memory.heap.init, value=268435456
type=GAUGE, name=jvm.memory.heap.max, value=1062338560
type=GAUGE, name=jvm.memory.heap.usage, value=0.09121857348376773
type=GAUGE, name=jvm.memory.heap.used, value=96905008

type=METER, name=org.jasig.cas.CentralAuthenticationServiceImpl.CREATE_TICKET_GRANTING_TICKET_METER, count=0, mean_rate=0.0, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/millisecond

type=METER, name=org.jasig.cas.CentralAuthenticationServiceImpl.DESTROY_TICKET_GRANTING_TICKET_METER, count=0, mean_rate=0.0, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/millisecond

type=TIMER, name=org.jasig.cas.CentralAuthenticationServiceImpl.GRANT_SERVICE_TICKET_TIMER, count=0, min=0.0, max=0.0, mean=0.0, stddev=0.0, median=0.0, p75=0.0, p95=0.0, p98=0.0, p99=0.0, p999=0.0, mean_rate=0.0, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/millisecond, duration_unit=milliseconds

```

### Viewing Metrics on the Web
The CAS web application exposes a `/statistics` endpoint that can be used to view metrics and stats in the browser. The endpoint is protected by pac4j, and the access rules are placed inside the `cas.properties` file:

```bash
# security configuration based on IP address to access the /status and /statistics pages
# cas.securityContext.adminpages.ip=127\.0\.0\.1|0:0:0:0:0:0:0:1
```

Once access is granted, the following sub-endpoints can be used to query the CAS server's status and metrics:

#### `/statistics/ping`
Reports back `pong` to indicate that the CAS server is running.

#### `/statistics/metrics?pretty=true`
Reports back metrics and performance data. The optional `pretty` flag attempts to format the JSON output.

#### `/statistics/threads`
Reports back JVM thread info.

#### `/statistics/healthcheck`
Unused at this point, but may be used later to output health examinations of the CAS server's internals, such as ticket registry, etc.

## Routing logs to SysLog
CAS logging framework does have the ability to route messages to an external syslog instance. To configure this, you first to configure the `SysLogAppender` and then specify which messages needs to be routed over to this instance:

```xml
...
<Appenders>
    <Syslog name="SYSLOG" format="RFC5424" host="localhost" port="8514"
            protocol="TCP" appName="MyApp" includeMDC="true"
            facility="LOCAL0" enterpriseNumber="18060" newLine="true"
            messageId="Audit" id="App"/>
</Appenders>

...

<logger name="org.jasig" additivity="true">
    <level value="DEBUG" />
    <appender-ref ref="cas" />
    <appender-ref ref="SYSLOG" />
</logger>

```

You can also configure the remote destination output over SSL and specify the related keystore configuration:

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

For additional logging functionality, please refer to the Log4j configuration url or view
the [CAS Logging functionality](Logging.html).

### SSO Sessions Report

CAS also provides a `/statistics/ssosessions` endpoint that produces a report of all active non-expired SSO sessions. The output of this endpoint is a JSON representation of SSO sessions that is rendered into a modest visualization.
