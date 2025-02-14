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

This module has a  configuration strategy which by default auto-configures a hazelcast instance used by the ticket registry
implementation to retrieve a Hazelcast's map for its distributed tickets storage. Some aspects of hazelcast
configuration in this auto-configuration mode are controlled by CAS properties.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

Should the more fine-grained configuration need arise, there is an option to use a native Hazelcast configuration XML format
where all the configuration options exposed by Hazelcast are available. In order to trigger this configuration mode,
there are two basic steps required:

* Place native `hazelcast.xml` file containing the custom configuration for Hazelcast Instance into an external filesystem location
  of choice (`/etc/cas/hazelcast.xml` by convention)
* Configure the location in the list of CAS properties.

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
