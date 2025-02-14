---
layout: default
title: CAS - Hazelcast Ticket Registry
---

# Hazelcast Ticket Registry

Hazelcast Ticket Registry is a distributed ticket registry implementation based on [Hazelcast distributed grid library](http://hazelcast.org/). The registry implementation is cluster-aware and is able to auto-join a cluster of all the CAS nodes that expose this registry. Hazelcast will use port auto-increment feature to assign a TCP port to each member of a cluster starting from initially provided arbitrary port (`5701` by default).

Hazelcast will evenly distribute the ticket data among all the members of a cluster in a very efficient manner. Also, by default, the data collection on each node is configured with 1 backup copy, so that Hazelcast will use it to make strong data consistency guarantees i.e. the loss of data on live nodes will not occur should any other *primary data owner* members die. The data will be re-partitioned among the remaining live cluster members.

This ticket registry implementation is enabled by simply including the module in the Maven overlay pom:

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-integration-hazelcast</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Configuration

Enable the registry via:

```xml
<alias name="hazelcastTicketRegistry" alias="ticketRegistry" />
```

This module has a flexible configuration strategy which by default auto-configures `HazelcastInstance` used by the TicketRegistry
implementation to retrieve Hazelcast's `IMap` instance for its distributed tickets storage. Some aspects of `HazelcastInstance`
configuration in this auto-configuration mode are controlled by the following exposed properties which could be set via
an external `cas.properties` file (with sensible defaults for all the properties if not explicitly set):

```properties
# hz.config.location=file:/etc/cas/hazelcast.xml
# hz.mapname=tickets
# hz.cluster.logging.type=slf4j
# hz.cluster.portAutoIncrement=true
# hz.cluster.port=5701
# hz.cluster.members=cas1.example.com,cas2.example.com
# hz.cluster.tcpip.enabled=true
# hz.cluster.multicast.enabled=false
# hz.cluster.max.heapsize.percentage=85
# hz.cluster.max.heartbeat.seconds=300
# hz.cluster.eviction.percentage=10
# hz.cluster.eviction.policy=LRU
# hz.cluster.instance.name=${host.name}
# hz.cluster.logging.type=slf4j
# hz.cluster.backupCount=1
# hz.cluster.asyncBackupCount=0
```

Should the more fine-grained configuration need arise, there is an option to use a native Hazelcast configuration XML format
where all the configuration options for `HazelcastInstance` exposed by Hazelcast are available. In order to trigger this configuration mode,
there are two basic steps required:

* Place native `hazelcast.xml` file containing the custom configuration for Hazelcast Instance into an external filesystem location
  of choice (`/etc/cas/hazelcast.xml` by convention).

* Indicate the location of the external native config file by the following property:

```properties
hz.config.location=file:/etc/cas/hazelcast.xml
```

Here's a simple example of `hazelcast.xml` that configures AWS cluster join strategy instead of a default TCP/IP one:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<hazelcast xsi:schemaLocation="http://www.hazelcast.com/schema/config hazelcast-config-3.6.xsd"
           xmlns="http://www.hazelcast.com/schema/config"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <network>
        <port auto-increment="true" port-count="100">5701</port>
        <join>
            <multicast enabled="false">
                <multicast-group>224.2.2.3</multicast-group>
                <multicast-port>55327</multicast-port>
            </multicast>
            <tcp-ip enabled="false">
                <member-list>
                    <member>127.0.0.1</member>
                </member-list>
            </tcp-ip>
            <aws enabled="true">
                <access-key>my-access-key</access-key>
                <secret-key>my-secret-key</secret-key>
                <!--optional, default is us-east-1 -->
                <region>us-west-1</region>
                <!--optional, default is ec2.amazonaws.com. If set, region shouldn't be set as it will override this property -->
                <host-header>ec2.amazonaws.com</host-header>
                <!-- optional, only instances belonging to this group will be discovered, default will try all running instances -->
                <security-group-name>hazelcast-sg</security-group-name>
                <tag-key>type</tag-key>
                <tag-value>hz-nodes</tag-value>
            </aws>
        </join>
    </network>

    <map name="tickets">
        <max-idle-seconds>28800</max-idle-seconds>
        <!--
            Valid values are:
            NONE (no eviction),
            LRU (Least Recently Used),
            LFU (Least Frequently Used).
            NONE is the default.
        -->
        <eviction-policy>LFU</eviction-policy>
        <!--
            Maximum size of the map. When max size is reached,
            map is evicted based on the policy defined.
            Any integer between 0 and Integer.MAX_VALUE. 0 means
            Integer.MAX_VALUE. Default is 0.
        -->
        <max-size policy="USED_HEAP_PERCENTAGE">85</max-size>
        <!--
            When max. size is reached, specified percentage of
            the map will be evicted. Any integer between 0 and 100.
            If 25 is set for example, 25% of the entries will
            get evicted.
        -->
        <eviction-percentage>10</eviction-percentage>
    </map>

</hazelcast>
```

For more information on the Hazelcast configuration options available, refer to [the Hazelcast documentation](http://docs.hazelcast.org/docs/3.6/manual/html/configuringhazelcast.html)

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
