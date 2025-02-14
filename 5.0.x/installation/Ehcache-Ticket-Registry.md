---
layout: default
title: CAS - Ehcache Ticket Registry
---

# Ehcache Ticket Registry
Ehcache integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-ehcache-ticket-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

This registry stores tickets in an [Ehcache](http://ehcache.org/) instance.


## Distributed Cache
Distributed caches are recommended for HA architectures since they offer fault tolerance in the ticket storage
subsystem. A single cache instance is created to house all types of tickets, and is synchronously replicated 
across the cluster of nodes that are defined in the configuration. 


### RMI Replication
Ehcache supports [RMI](http://docs.oracle.com/javase/6/docs/technotes/guides/rmi/index.html)
replication for distributed caches composed of two or more nodes. To learn more about RMI
replication with Ehcache, [see this resource](http://ehcache.org/documentation/user-guide/rmi-replicated-caching).

#### Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

The Ehcache configuration for `ehcache-replicated.xml` mentioned in the config follows.

```xml

<ehcache name="ehCacheTicketRegistryCache"
    updateCheck="false"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd">

        <diskStore path="java.io.tmpdir/cas"/>

        <!-- Automatic Peer Discovery
        <cacheManagerPeerProviderFactory
        class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
        properties="peerDiscovery=automatic, multicastGroupAddress=230.0.0.1, multicastGroupPort=4446, timeToLive=32"
        propertySeparator="," />
        -->

        <!-- Manual Peer Discovery -->
        <cacheManagerPeerProviderFactory
            class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
            properties="peerDiscovery=manual,rmiUrls=//localhost:41001/org.apereo.cas.ticket.TicketCache" />
        <cacheManagerPeerListenerFactory
            class="net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory"
            properties="port=41001,remoteObjectPort=41002" />
</ehcache>
```

### Eviction Policy

Ehcache manages the internal eviction policy of cached objects via the idle and alive settings.
These settings control the general policy of the cache that is used to store various ticket types. In general,
you need to ensure the cache is alive long enough to support the individual expiration policy of tickets, and let
CAS clean the tickets as part of its own cleaner. 


### Troubleshooting Guidelines

* You will need to ensure that network communication across CAS nodes is allowed and no firewall or other component
 is blocking traffic.

* If you are running this on a server with active firewalls, you will probably need to specify
a fixed `remoteObjectPort`, within the `cacheManagerPeerListenerFactory`.
* Depending on environment settings and version of Ehcache used, you may also have to adjust the
`shared` setting .
* Ensure that each cache manager specified a name that matches the Ehcache configuration itself.
* You may also need to adjust your expiration policy to allow for a larger time span, specially
for service tickets depending on network traffic and communication delay across CAS nodes particularly
in the event that a node is trying to join the cluster.
