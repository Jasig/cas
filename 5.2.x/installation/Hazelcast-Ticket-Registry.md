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
implementation to build and retrieve Hazelcast's maps for its distributed tickets storage. Some aspects of hazelcast
configuration in this auto-configuration mode are controlled by CAS properties.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#hazelcast-ticket-registry).

<div class="alert alert-warning"><strong>Session Monintoring</strong><p>Be aware that under very heavy load and given a very large collection of tickets over time, <a href="Configuring-Monitoring.html">session monitoring capabilities</a> of CAS that report back ticket statistics based on the underlying Hazelcast ticket registry may end up timing out. This is due to the concern that Hazelcast attempts to run distributed queries across the entire network to collect, analyze and aggregate tickets which may be still active or in flux. If you do experience this behavior, it likely is preferable to turn off the session monitor.
</p></div>

For more information on the Hazelcast configuration options available,
refer to [the Hazelcast configuration documentation](http://docs.hazelcast.org/docs/3.7/manual/html-single/index.html#hazelcast-configuration)

## Multicast Auto Discovery

With the multicast auto-discovery mechanism, Hazelcast allows cluster members to find each other using multicast communication. The cluster members do not need to know the concrete addresses of the other members, as they just multicast to all the other members for listening. Whether multicast is possible or allowed **depends on your environment**.

Pay special attention to timeouts when multicast is enabled. Multicast timeout specifies the time in seconds that a member should wait for a valid multicast response from another member running in the network before declaring itself the leader member (the first member joined to the cluster) and creating its own cluster. This only applies to the startup of members where no leader has been assigned yet. If you specify a high value such as 60 seconds, it means that until a leader is selected each member will wait 60 seconds before moving on. Be careful when providing a high value. Also, be careful not to set the value too low, or the members might give up too early and create their own cluster.

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