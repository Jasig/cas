---
layout: default
title: CAS - Ehcache Ticket Registry
category: Ticketing
---

# Ehcache v3 Ticket Registry

Ehcache 3.x integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-ehcache3-ticket-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

This registry stores tickets using the [Ehcache 3.x](http://ehcache.org/) caching library 
and [an optional Terracotta cluster](https://www.ehcache.org/documentation/3.3/clustered-cache.html).

## In-memory store with disk persistence

Ehcache 3.x doesn't support distributing caching without Terracotta so using it without pointing at a Terracotta 
server or cluster doesn't support using more than one CAS server at a time. The location and size of the disk caches 
can be configured using the root-directory and per-cache-size-on-disk properties. If the persist-on-disk property
is set to true then the caches will survive a restart. 

### Terracotta Clustering

By pointing this Ehcache module at a Terracotta server then multiple CAS servers can share tickets. CAS uses `autocreate` 
to create the Terracotta cluster configuration. An easy way to run a Terracotta server is to use the [docker container](https://github.com/Terracotta-OSS/docker).

```bash
docker run --rm --name tc-server -p 9410:9410 -d \
 --env OFFHEAP_RESOURCE1_NAME=main \
 --env OFFHEAP_RESOURCE2_NAME=extra \
 --env OFFHEAP_RESOURCE1_SIZE=256 \
 --env OFFHEAP_RESOURCE2_SIZE=16 \
terracotta/terracotta-server-oss:5.6.4
```

Running a Terracotta cluster on Kubernetes can be done easily using the Terracotta [helm chart](https://github.com/helm/charts/tree/master/stable/terracotta).

#### Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#ehcache-3-ticket-registry).
CAS currently doesn't support or require an XML configuration to configure Ehcache. 

### Eviction Policy

Ehcache can be configured as "eternal" in which case CAS's regular cleaning process will remove expired tickets. If the 
eternal property is set to false then storage timeouts will be set based on the metadata for the individual caches.

# Ehcache v2 Ticket Registry

Due to the relatively unsupported status of the Ehcache 2.x code base, this module is deprecated and will likely be 
removed in a future CAS release. Unlike the Ehcache 3.x library, it can replicate directly between CAS servers without
needing an external cache cluster (e.g. Terracotta in Ehcache 3.x).

<div class="alert alert-warning"><strong>Usage</strong>
<p><strong>This feature is deprecated and is scheduled to be removed in the future.</strong> If you can, consider using
the Ehcache v3 ticket registry functionality in CAS to handle this integration.</p>
</div>

Ehcache integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-ehcache-ticket-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

This registry stores tickets using [Ehcache](http://ehcache.org/) version 2.x library.

## Distributed Cache

Distributed caches are recommended for HA architectures since they offer fault tolerance in the ticket storage
subsystem. A single cache instance is created to house all types of tickets, and is synchronously replicated
across the cluster of nodes that are defined in the configuration.

### RMI Replication

Ehcache supports [RMI](https://docs.oracle.com/javase/tutorial/rmi/index.html)
replication for distributed caches composed of two or more nodes. To learn more about RMI
replication with Ehcache, [see this resource](https://www.ehcache.org/documentation/2.8/replication/rmi-replicated-caching.html).

#### Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#ehcache-ticket-registry).

The Ehcache configuration for `ehcache-replicated.xml` mentioned in the config follows. 
Note that `${ehcache.otherServer}` would be replaced by a system property: `-Dehcache.otherserver=cas2`.

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
        properties="peerDiscovery=manual,rmiUrls=//${ehcache.otherServer}:41001/proxyGrantingTicketsCache| \
            //${ehcache.otherServer}:41001/ticketGrantingTicketsCache|//${ehcache.otherServer}:41001/proxyTicketsCache| \
            //${ehcache.otherServer}:41001/oauthCodesCache|//${ehcache.otherServer}:41001/samlArtifactsCache| \
            //${ehcache.otherServer}:41001/oauthDeviceUserCodesCache|//${ehcache.otherServer}:41001/samlAttributeQueryCache| \
            //${ehcache.otherServer}:41001/oauthAccessTokensCache|//${ehcache.otherServer}:41001/serviceTicketsCache| \
            //${ehcache.otherServer}:41001/oauthRefreshTokensCache|//${ehcache.otherServer}:41001/transientSessionTicketsCache| \
            //${ehcache.otherServer}:41001/oauthDeviceTokensCache" />

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
