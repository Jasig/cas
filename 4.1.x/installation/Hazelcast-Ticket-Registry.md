---
layout: default
title: CAS - Hazelcast Ticket Registry
---

# Hazelcast Ticket Registry

Hazelcast Ticket Registry is a distributed ticket registry implementation based on [Hazelcast distributed grid library](http://hazelcast.org/). The registry implementation is cluster-aware and is able to auto-join a cluster of all the CAS nodes that expose this registry. Hazelcast will use port auto-increment feature to assign a TCP port to each member of a cluster starting from initially provided arbitrary port (`5701` by default).

Hazelcast will evenly distribute the ticket data among all the members of a cluster in a very efficient manner. Also, by default, the data collection on each node is configured with 1 backup copy, so that Hazelcast will use it to make strong data consistency guarantees i.e. the loss of data on live nodes will not occur should any other *primary data owner* members die. The data will be re-partitioned among the remaining live cluster members.

This ticket registry implementation is enabled by simply including the module in the Maven overlay pom:

{% highlight xml %}
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-integration-hazelcast</artifactId>
    <version>${cas.version}</version>
</dependency>
{% endhighlight %}

## Configuration

This implementation auto-configures most of the internal details of the underlying Hazelcast instance and the distributed `IMap` for tickets storage.
The only required configuration value on each CAS node in the cluster is a comma-separated list of *ALL* the member nodes defined in the configuration 
property `hz.cluster.members` (in `cas.properties` file). For example: `hz.cluster.members=cas1.example.com,cas2.example.com`

Other optional properties that could be set are:

* `hz.cluster.port` (default value is `5701`)
* `hz.cluster.portAutoIncrement` (default value is `true`)
* TGT time to live value for this implementation is set via `tgt.maxTimeToLiveInSeconds` and defaults to `28800`
* ST time to live value for this implementation is set via `st.timeToKillInSeconds` and defaults to `10`
 
## Logging
To enable additional logging for the registry, configure the log4j configuration file to add the following
levels:

{% highlight xml %}
...
<Logger name="com.hazelcast" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</Logger>
...
{% endhighlight %}
  
  
