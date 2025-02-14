---
layout: default
title: CAS - Hazelcast Ticket Registry
---

# Hazelcast Ticket Registry

Hazelcast Ticket Registry is a distributed ticket registry implementation
based on [Hazelcast distributed grid library](http://hazelcast.org/). The registry implementation is
cluster-aware and is able to auto-join a cluster of all the CAS nodes that expose this registry.
Hazelcast will use port auto-increment feature to assign a TCP port to each member of a cluster starting
from initially provided arbitrary port (`5701` by default).

Hazelcast will evenly distribute the ticket data among all the members of a cluster in a very
efficient manner. Also, by default, the data collection on each node is configured with 1 backup copy,
so that Hazelcast will use it to make strong data consistency guarantees i.e. the loss of data on
live nodes will not occur should any other *primary data owner* members die. The data will be
re-partitioned among the remaining live cluster members.

Support is enabled by the following module:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-hazelcast-ticket-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Configuration

This module has a configuration strategy which by default auto-configures a hazelcast instance used by the ticket registry
implementation to retrieve a Hazelcast's map for its distributed tickets storage. Some aspects of hazelcast
configuration in this auto-configuration mode are controlled by CAS properties.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#hazelcast-ticket-registry).

<div class="alert alert-warning"><strong>Session Monintoring</strong><p>Be aware that under very heavy load and given a very large collection of tickets over time, <a href="Configuring-Monitoring.html">session monitoring capabilities</a> of CAS that report back ticket statistics based on the underlying Hazelcast ticket registry may end up timing out. This is due to the concern that Hazelcast attempts to run distributed queries across the entire network to collect, analyze and aggregate tickets which may be still active or in flux. If you do experience this behavior, it likely is preferable to turn off the session monitor.
</p></div>

For more information on the Hazelcast configuration options available,
refer to [the Hazelcast configuration documentation](http://docs.hazelcast.org/docs/3.7/manual/html-single/index.html#hazelcast-configuration)

## Logging

To enable additional logging for the registry, configure the log4j configuration file to add the following
levels:

```xml
...
<AsyncLogger name="com.hazelcast" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```
