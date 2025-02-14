---
layout: default
title: CAS - Monitoring & Statistics
---

# Monitoring
The CAS server exposes a `/status` endpoint that may be used to inquire about the health and general state of the software. Access to the endpoint is secured by Spring Security at `src/main/webapp/WEB-INF/spring-configuration/securityContext.xml`:

{% highlight xml %}
<sec:http pattern="/status/**" entry-point-ref="notAuthorizedEntryPoint" use-expressions="true" auto-config="true">
    <sec:intercept-url pattern="/status" access="${cas.securityContext.status.access}" />
</sec:http>
{% endhighlight %}

Access is granted the following settings in `cas.properties` file:

{% highlight bash %}
# Spring Security's EL-based access rules for the /status URI of CAS that exposes health check information
cas.securityContext.status.access=hasIpAddress('127.0.0.1')

{% endhighlight %}


## Sample Output

{% highlight bash %}
Health: OK

    1.MemoryMonitor: OK - 322.13MB free, 495.09MB total.
{% endhighlight %}


## Internal Configuration Report

CAS also provides a `/status/config` endpoint that produces a report of the runtime CAS configuration, which includes all components that are under the `org.jasig`
package as well as settings defined in the `cas.properties` file. The output of this endpoint is a JSON representation of the runtime that is rendered into a modest visualization:

![](https://cloud.githubusercontent.com/assets/1205228/7085296/35819ff0-df2a-11e4-9818-9119fd30588e.jpg)

# Statistics
Furthermore, the CAS web application has the ability to present statistical data about the runtime environment as well as ticket registry's performance.

The CAS server exposes a `/statistics` endpoint that may be used to inquire about the runtime state of the software. Access to the endpoint is secured by Spring Security at `src/main/webapp/WEB-INF/spring-configuration/securityContext.xml`:

{% highlight xml %}
<sec:http pattern="/statistics/**" entry-point-ref="notAuthorizedEntryPoint" use-expressions="true" auto-config="true">
 <sec:intercept-url pattern="/statistics" access="${cas.securityContext.statistics.access}" />
</sec:http>
{% endhighlight %}

Access is granted the following settings in `cas.properties` file:

{% highlight bash %}
# Spring Security's EL-based access rules for the /statistics URI of CAS that exposes stats about the CAS server
cas.securityContext.statistics.access=hasIpAddress('127.0.0.1')

{% endhighlight %}

![](http://i.imgur.com/8CXPgOC.png)

## Performance Statistics
CAS also uses the [Dropwizard Metrics framework](https://dropwizard.github.io/metrics/), that provides set of utilities for calculating and displaying performance statistics. 

### Configuration
The metrics configuration is controlled via the `/src/main/webapp/WEB-INF/spring-configuration/metricsContext.xml` file. The configuration will output all performance-related data and metrics to the logging framework. The reporting interval can be configured via the `cas.properties` file:

{% highlight bash %}

# Define how often should metric data be reported. Default is 30 seconds.
# metrics.refresh.interval=30s

{% endhighlight %}

Various metrics can also be reported via JMX. Metrics are exposes via JMX MBeans. 

{% highlight xml %}

<metrics:reporter type="jmx" metric-registry="metrics" />

{% endhighlight %}

To explore this you can use VisualVM (which ships with most JDKs as jvisualvm) with the VisualVM-MBeans plugins installed or JConsole (which ships with most JDKs as jconsole):

![](http://i.imgur.com/g8fmUlE.png)

Additionally, various metrics on JVM performance and data are also reported. The metrics contain a number of reusable gauges and metric sets which allow you to easily instrument JVM internals.

{% highlight xml %}

<metrics:register metric-registry="metrics">
    <bean metrics:name="jvm.gc" class="com.codahale.metrics.jvm.GarbageCollectorMetricSet" />
    <bean metrics:name="jvm.memory" class="com.codahale.metrics.jvm.MemoryUsageGaugeSet" />
    <bean metrics:name="jvm.thread-states" class="com.codahale.metrics.jvm.ThreadStatesGaugeSet" />
    <bean metrics:name="jvm.fd.usage" class="com.codahale.metrics.jvm.FileDescriptorRatioGauge" />
</metrics:register>

{% endhighlight %}

Supported metrics include:

- Run count and elapsed times for all supported garbage collectors
- Memory usage for all memory pools, including off-heap memory
- Breakdown of thread states, including deadlocks
- File descriptor usage
- ...

### Loggers
All performance data and metrics are routed to a log file via the Log4j configuration:

{% highlight xml %}
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

{% endhighlight %}


### Sample Output
{% highlight bash %}
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

type=TIMER, name=org.jasig.cas.CentralAuthenticationServiceImpl.VALIDATE_SERVICE_TICKET_TIMER, count=0, min=0.0, max=0.0, mean=0.0, stddev=0.0, median=0.0, p75=0.0, p95=0.0, p98=0.0, p99=0.0, p999=0.0, mean_rate=0.0, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/millisecond, duration_unit=milliseconds

{% endhighlight %}

### Viewing Metrics on the Web
The CAS web application exposes a `/statistics` endpoint that can be used to view metrics and stats in the browser. The endpoint is protected by Spring Security, and the access rules are placed inside the `cas.properties` file:

{% highlight bash %}
# Spring Security's EL-based access rules for the /statistics URI of CAS that exposes stats about the CAS server
cas.securityContext.statistics.access=hasIpAddress('127.0.0.1')
{% endhighlight %}

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

{% highlight xml %}
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

{% endhighlight %}

You can also configure the remote destination output over SSL and specify the related keystore configuration:

{% highlight xml %}
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

{% endhighlight %}

For additional logging functionality, please refer to the Log4j configuration url or view
the [CAS Logging functionality](Logging.html). 

### SSO Sessions Report

CAS also provides a `/statistics/ssosessions` endpoint that produces a report of all active non-expired SSO sessions. The output of this endpoint is a JSON representation of SSO sessions that is rendered into a modest visualization:

![](https://cloud.githubusercontent.com/assets/1205228/6801195/fcf77186-d1e2-11e4-8059-cfa1d7e80d83.PNG)

By default, ticket-granting ticket ids are not shown. This behavior can be controlled via `cas.properties`:

{% highlight properties %}

##
# Reports
#
# Setting to whether include the ticket granting ticket id in the report
# sso.sessions.include.tgt=false

{% endhighlight %}

