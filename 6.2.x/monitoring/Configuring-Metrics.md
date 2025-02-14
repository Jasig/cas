---
layout: default
title: CAS - Metrics
category: Monitoring & Statistics
---

# CAS Metrics

CAS via Spring Boot registers the following core metrics when applicable:

JVM metrics, report utilization of:

- Various memory and buffer pools
- Statistics related to garbage collection
- Threads utilization
- Number of classes loaded/unloaded
- CPU metrics
- File descriptor metrics
- Logback metrics: record the number of events logged to Logback at each level
- Uptime metrics: report a gauge for uptime and a fixed gauge representing the application’s absolute start time
- Tomcat metrics
- Spring Integration metrics

Support is enabled by including the following module in the Overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-metrics</artifactId>
     <version>${cas.version}</version>
</dependency>
```

Auto-configuration enables the instrumentation of all available DataSource objects 
with a metric named `jdbc`. Data source instrumentation results in gauges representing the 
currently active, maximum allowed, and minimum allowed connections in the pool. 
Each of these gauges has a name that is prefixed by `jdbc`. Metrics are also tagged by the name of the `DataSource` 
computed based on the bean name. Also, Hikari-specific metrics are exposed with a `hikaricp` prefix. Each metric is tagged by the name of the Pool.

Auto-configuration enables the instrumentation of all available Caches on startup with metrics prefixed with cache. 
Cache instrumentation is standardized for a basic set of metrics. Additional, cache-specific metrics are also available.

Auto-configuration will enable the instrumentation of all available RabbitMQ connection factories with a metric named `rabbitmq`.

CAS Metrics are accessed and queried using the CAS actuator admin endpoints. 
Navigating to the endpoint displays a list of available meter names. 
You can drill down to view information about a particular meter by providing its name as a selector.

[See this guide](Monitoring-Statistics.html) to learn more.

## Administrative Endpoints

The following endpoints are provided by CAS:
 
| Endpoint                 | Description
|--------------------------|------------------------------------------------
| `statistics`             | Exposes statistics data on tickets, memory, server availability and uptime, etc.

## Custom Metrics

To register custom metrics, inject `MeterRegistry` into your component, as shown in the following example:

```java
public class Dictionary {
    private final List<String> words = new CopyOnWriteArrayList<>();

    Dictionary(final MeterRegistry registry) {
        registry.gaugeCollectionSize("dictionary.size", Tags.empty(), this.words);
    }
}
```

If you find that you repeatedly instrument a suite of metrics across components or applications,
 you may encapsulate this suite in a `MeterBinder` implementation. By default, metrics 
 from all `MeterBinder` beans will be automatically bound to the Spring-managed `MeterRegistry`.

# Customizing Metrics

If you need to apply customizations to specific Meter instances you can use the `io.micrometer.core.instrument.config.MeterFilter` interface. 
By default, all `MeterFilter` beans will be automatically applied to the micrometer `MeterRegistry.Config`.

For example, if you want to rename the `mytag.region` tag to `mytag.area` for all meter IDs beginning with `com.example`, you can do the following:

```java
@Bean
public MeterFilter renameRegionTagMeterFilter() {
    return MeterFilter.renameTag("com.example", "mytag.region", "mytag.area");
}
```

## Storage

CAS metrics may be routed to varying types of databases for storage and analytics. The following options are available:

- Simple (In Memory)
- Graphite
- Ganglia
- JMX
- Atlas
- Signal FX
- Statsd
- InfluxDb
- Prometheus
- Wavefront
- New Relic
- CloudWatch
- ...

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#metrics).
