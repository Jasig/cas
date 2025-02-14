---
layout: default
title: CAS - Ehcache Ticket Registry
---

# Ehcache Ticket Registry
Ehcache integration is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
     <groupId>org.jasig.cas</groupId>
     <artifactId>cas-server-integration-ehcache</artifactId>
     <version>${cas.version}</version>
</dependency>
```

`EhCacheTicketRegistry` stores tickets in an [Ehcache](http://ehcache.org/) instance.


## Distributed Cache
Distributed caches are recommended for HA architectures since they offer fault tolerance in the ticket storage
subsystem. The registry maintains service tickets and ticket-granting tickets in two separate caches, so that:

* Ticket Granting Tickets remain valid for a long time, replicated asynchronously
* Service Tickets are short lived and must be replicated right away because the requests
to validate them may very likely arrive at different CAS cluster nodes


### RMI Replication
Ehcache supports [RMI](http://docs.oracle.com/javase/6/docs/technotes/guides/rmi/index.html)
replication for distributed caches composed of two or more nodes. To learn more about RMI
replication with Ehcache, [see this resource](http://ehcache.org/documentation/user-guide/rmi-replicated-caching).

Enable the registry via:

```xml
<alias name="ehcacheTicketRegistry" alias="ticketRegistry" />
```

#### Configuration

```properties
# ehcache.config.file=classpath:ehcache-replicated.xml
# ehcache.cachemanager.shared=false
# ehcache.cachemanager.name=ticketRegistryCacheManager
# ehcache.disk.expiry.interval.seconds=0
# ehcache.disk.persistent=false
# ehcache.eternal=false
# ehcache.max.elements.memory=10000
# ehcache.max.elements.disk=0
# ehcache.eviction.policy=LRU
# ehcache.overflow.disk=false
# ehcache.cache.st.name=org.jasig.cas.ticket.ServiceTicket
# ehcache.cache.st.timeIdle=0
# ehcache.cache.st.timeAlive=300
# ehcache.cache.tgt.name=org.jasig.cas.ticket.TicketGrantingTicket
# ehcache.cache.tgt.timeIdle=7201
# ehcache.cache.tgt.timeAlive=0
# ehcache.cache.loader.async=true
# ehcache.cache.loader.chunksize=5000000
# ehcache.repl.async.interval=10000
# ehcache.repl.async.batch.size=100
# ehcache.repl.sync.puts=true
# ehcache.repl.sync.putscopy=true
# ehcache.repl.sync.updates=true
# ehcache.repl.sync.updatesCopy=true
# ehcache.repl.sync.removals=true
```

The Ehcache configuration for `ehcache-replicated.xml` mentioned in the config follows.

```xml

<ehcache name="ehCacheTicketRegistryCache"
    updateCheck="false"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd">

  <diskStore path="java.io.tmpdir/cas"/>

  <!--
     | Automatic peer discovery
     | See http://ehcache.org/documentation/user-guide/rmi-replicated-caching#automatic-peer-discovery
     | for more information.
     -->
  <!--
  <cacheManagerPeerProviderFactory
        class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
        properties="peerDiscovery=automatic, multicastGroupAddress=230.0.0.1, multicastGroupPort=4446, timeToLive=32"
        propertySeparator="," />
  -->

  <!--
     | Manual peer discovery
     | See http://ehcache.org/documentation/user-guide/rmi-replicated-caching#manual-peer-discovery-manual-peer-discovery
     | for more information
     -->
  <cacheManagerPeerProviderFactory
      class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
      properties="peerDiscovery=manual,rmiUrls=//peer-2:41001/cas_st|//peer-3:41001/cas_st|//peer-2:41001/cas_tgt|//peer-3:41001/cas_tgt" />
  <cacheManagerPeerListenerFactory
      class="net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory"
      properties="port=41001,remoteObjectPort=41002" />
</ehcache>

```

### Eviction Policy

Ehcache manages the internal eviction policy of cached objects via `timeToIdle` and `timeToLive` settings.
The default CAS ticket registry cleaner is then not needed, but could be used to enable
[CAS single logout functionality](Logout-Single-Logout.html), if required.

There have been reports of cache eviction problems when tickets are expired, but haven't been
removed from the cache due to ehache configuration. This can be a problem because old ticket
references "hang around" in the cache despite being expired. In other words,
Ehcache does [inline eviction](http://lists.terracotta.org/pipermail/ehcache-list/2011-November/000423.html)
where expired objects in the cache object won't be collected from memory until the cache max size is reached
or the expired entry is explicitly accessed. To reclaim memory on expired tickets, cache eviction
policies are must be carefully configured to avoid memory creep. Disk offload and/or a more
aggressive eviction could provide a suitable workaround.


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
