---
layout: default
title: CAS - Metrics
---

# CAS Metrics

Supported metrics include:

- Run count and elapsed times for all supported garbage collectors
- Memory usage for all memory pools, including off-heap memory
- Breakdown of thread states, including deadlocks
- File descriptor usage
- ...

### Metric Refresh Interval

The metrics reporting interval can be configured via CAS properties.
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#metrics--performance-stats).

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

<CasAppender name="casPerf">
    <AppenderRef ref="perfFileAppender" />
</CasAppender>
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

type=METER, name=org.apereo.cas.DefaultCentralAuthenticationService.CREATE_TICKET_GRANTING_TICKET_METER, count=0,
mean_rate=0.0, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/millisecond

type=METER, name=org.apereo.cas.DefaultCentralAuthenticationService.DESTROY_TICKET_GRANTING_TICKET_METER,
count=0, mean_rate=0.0, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/millisecond

type=TIMER, name=org.apereo.cas.DefaultCentralAuthenticationService.GRANT_SERVICE_TICKET_TIMER, count=0,
min=0.0, max=0.0, mean=0.0, stddev=0.0, median=0.0, p75=0.0, p95=0.0, p98=0.0, p99=0.0, p999=0.0,
mean_rate=0.0, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/millisecond, duration_unit=milliseconds
```
