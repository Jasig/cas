---
layout: default
title: CAS - Ehcache Ticket Registry
---

# Ehcache Ticket Registry
Ehcache integration is enabled by including the following dependency in the Maven WAR overlay:

    <dependency>
         <groupId>org.jasig.cas</groupId>
         <artifactId>cas-server-integration-ehcache</artifactId>
         <version>${cas.version}</version>
    </dependency>

`EhCacheTicketRegistry` stores tickets in an [Ehcache](http://ehcache.org/) instance.

We present two configurations:

1. Single instance memory-backed cache with disk overflow for simple cases.
2. Distributed cache with peer replication over RMI for HA deployments.


## Memory Cache with Disk Overflow
The following Spring configuration provides a template for `ticketRegistry.xml`.
{% highlight xml %}
<bean id="ticketRegistry"
      class="org.jasig.cas.ticket.registry.EhCacheTicketRegistry"
      p:serviceTicketsCache-ref="serviceTicketsCache"
      p:ticketGrantingTicketsCache-ref="ticketGrantingTicketsCache" />

<bean id="abstractTicketCache" abstract="true"
      class="org.springframework.cache.ehcache.EhCacheFactoryBean"
      p:cacheManager-ref="cacheManager"
      p:diskExpiryThreadIntervalSeconds="0"
      p:diskPersistent="false"
      p:eternal="false"
      p:maxElementsInMemory="10000"
      p:maxElementsOnDisk="20000"
      p:memoryStoreEvictionPolicy="LRU"
      p:overflowToDisk="true" />

<bean id="serviceTicketsCache"
      class="org.springframework.cache.ehcache.EhCacheFactoryBean"
      parent="abstractTicketCache"
      p:cacheName="cas_st"
      p:timeToIdle="0"
      p:timeToLive="300" />

<bean id="ticketGrantingTicketsCache"
      class="org.springframework.cache.ehcache.EhCacheFactoryBean"
      p:cacheName="cas_tgt"
      parent="abstractTicketCache"
      p:timeToIdle="0"
      p:timeToLive="7201" />

<bean id="cacheManager"
      class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean"
      p:configLocation="classpath:ehcache-failsafe.xml"
      p:shared="false"
      p:cacheManagerName="ticketRegistryCacheManager" />
{% endhighlight %}

The Ehcache configuration file `ehcache-failsafe.xml` mentioned in the Spring configuration above:
{% highlight xml %}
<ehcache updateCheck="false"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd">

  <diskStore path="java.io.tmpdir/cas"/>

</ehcache>
{% endhighlight %}


## Distributed Cache 
Distributed caches are recommended for HA architectures since they offer fault tolerance in the ticket storage subsystem. The registry maintains service tickets and ticket-granting tickets in two separate caches, so that:

* Ticket Granting Tickets remain valid for a long time, replicated asynchronously
* Service Tickets are short lived and must be replicated right away because the requests to validate them may very likely arrive at different CAS cluster nodes


### RMI Replication
Ehcache supports [RMI](http://docs.oracle.com/javase/6/docs/technotes/guides/rmi/index.html) replication for distributed caches composed of two or more nodes. To learn more about RMI replication with Ehcache, [see this resource](http://ehcache.org/documentation/user-guide/rmi-replicated-caching).


#### Spring configuration template for `ticketRegistry.xml`.
{% highlight xml %}
<bean id="ticketRegistry"
      class="org.jasig.cas.ticket.registry.EhCacheTicketRegistry"
      p:serviceTicketsCache-ref="serviceTicketsCache"
      p:ticketGrantingTicketsCache-ref="ticketGrantingTicketsCache" />

<bean id="abstractTicketCache" abstract="true"
      class="org.springframework.cache.ehcache.EhCacheFactoryBean"
      p:cacheManager-ref="cacheManager"
      p:diskExpiryThreadIntervalSeconds="0"
      p:diskPersistent="false"
      p:eternal="false"
      p:maxElementsInMemory="10000"
      p:maxElementsOnDisk="20000"
      p:memoryStoreEvictionPolicy="LRU"
      p:overflowToDisk="true"
      p:bootstrapCacheLoader-ref="ticketCacheBootstrapCacheLoader" />

<!-- MUST use synchronous repl for service tickets for correct behavior. -->
<bean id="serviceTicketsCache"
      class="org.springframework.cache.ehcache.EhCacheFactoryBean"
      parent="abstractTicketCache"
      p:cacheName="cas_st"
      p:timeToIdle="0"
      p:timeToLive="300"
      p:cacheEventListeners-ref="ticketRMISynchronousCacheReplicator" />

<bean id="ticketGrantingTicketsCache"
      class="org.springframework.cache.ehcache.EhCacheFactoryBean"
      parent="abstractTicketCache"
      p:cacheName="cas_tgt"
      p:timeToIdle="0"
      p:timeToLive="7201"
      p:cacheEventListeners-ref="ticketRMIAsynchronousCacheReplicator" />

<bean id="cacheManager"
      class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean"
      p:configLocation="classpath:ehcache-replicated.xml"
      p:shared="false"
      p:cacheManagerName="ticketRegistryCacheManager" />

<bean id="ticketRMISynchronousCacheReplicator"
      class="net.sf.ehcache.distribution.RMISynchronousCacheReplicator"
      c:replicatePuts="true"
      c:replicatePutsViaCopy="true"
      c:replicateUpdates="true"
      c:replicateUpdatesViaCopy="true"
      c:replicateRemovals="true" />

<bean id="ticketRMIAsynchronousCacheReplicator"
      class="net.sf.ehcache.distribution.RMIAsynchronousCacheReplicator"
      parent="ticketRMISynchronousCacheReplicator"
      c:replicationInterval="10000"
      c:maximumBatchSize="100" />

<bean id="ticketCacheBootstrapCacheLoader"
      class="net.sf.ehcache.distribution.RMIBootstrapCacheLoader"
      c:asynchronous="true"
      c:maximumChunkSize="5000000" />
{% endhighlight %}

The Ehcache configuration for `ehcache-replicated.xml` mentioned in the Spring config follows.
{% highlight xml %}
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
{% endhighlight %}

Use either manual or automatic peer discovery to assemble members of distributed cache. If manual discover is used
for configuration, the file would vary according to the node on which CAS is deployed. For that reason it may be
helpful to place the configuration file on the filesystem at a well-known location and reference it in the
`EhCacheManagerFactoryBean` above as follows:
{% highlight xml %}
<bean id="cacheManager"
      class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean"
      p:configLocation="file:/path/to/well-known/ehcache-replicated.xml"
      p:shared="false"
      p:cacheManagerName="ticketRegistryCacheManager" />
{% endhighlight %}



### JGroups Replication
[JGroups](http://ehcache.org/documentation/2.5/replication/jgroups-replicated-caching) can be used as the underlying mechanism for the replication operations in Ehcache. JGroups offers a very flexible protocol stack, reliable unicast, and multicast message transmission. On the down side JGroups can be complex to configure and some protocol stacks have dependencies on others.


#### Spring configuration template for `ticketRegistry.xml`.
The configuration is similar to above, except that ticket replicators and cache loaders need to be swapped out for their JGroups counterpart:

{% highlight xml %}
...
<bean id="ticketjgroupsSynchronousCacheReplicator" class="net.sf.ehcache.distribution.jgroups.JGroupsCacheReplicator">
    <constructor-arg name="replicatePuts" value="true"/> 
    <constructor-arg name="replicateUpdates" value="true"/>  
    <constructor-arg name="replicateUpdatesViaCopy" value="true"/>  
    <constructor-arg name="replicateRemovals" value="true"/>       
</bean>
 
<bean id="ticketjgroupsAsynchronousCacheReplicator" class="net.sf.ehcache.distribution.jgroups.JGroupsCacheReplicator" parent="ticketjgroupsSynchronousCacheReplicator">
    <constructor-arg name="asynchronousReplicationInterval" value="1000"/>  
</bean>
 
<bean id="ticketCacheBootstrapCacheLoader" class="net.sf.ehcache.distribution.jgroups.JGroupsBootstrapCacheLoader">
  <constructor-arg name="asynchronous" value="true"/>
  <constructor-arg name="maximumChunkSize" value="5000000"/>
</bean>
...
{% endhighlight %}

The Ehcache JGroups confguration itself needs to be altered to be similar to the following:

{% highlight xml %}
<ehcache name="ehCacheTicketRegistryCache"
    updateCheck="false"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.sf.net/ehcache.xsd">

   <diskStore path="java.io.tmpdir/cas"/>
   
   <!-- Using UDP multicast stack -->
   <cacheManagerPeerProviderFactory
        class="net.sf.ehcache.distribution.jgroups.JGroupsCacheManagerPeerProviderFactory"
        properties="connect=UDP(mcast_addr=231.12.21.132;mcast_port=45566;):PING:
        MERGE2:FD_SOCK:VERIFY_SUSPECT:pbcast.NAKACK:UNICAST:pbcast.STABLE:FRAG:pbcast.GMS"
        propertySeparator="::"
        />

</ehcache>
{% endhighlight %}

Your maven overlay `pom.xml` will also need to declare the following dependencies:
{% highlight xml %}
<dependency>
    <groupId>org.jgroups</groupId>
    <artifactId>jgroups</artifactId>
    <version>${jgroups.version}</version>
</dependency>

<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache-jgroupsreplication</artifactId>
    <version>${ehcache-jgroups.version}</version>
</dependency>                   
{% endhighlight %}



### Eviction Policy
Ehcache manages the internal eviction policy of cached objects via `timeToIdle` and `timeToLive` settings. This results of having *no need* for a Ticket Registry Cleaner.

There have been reports of cache eviction problems when tickets are expired, but haven't been removed from the cache due to ehache configuration. This can be a problem because old ticket references "hang around" in the cache despite being expired. In other words, Ehcache does [inline eviction](http://lists.terracotta.org/pipermail/ehcache-list/2011-November/000423.html) where expired objects in the cache object won't be collected from memory until the cache max size is reached or the expired entry is explicitly accessed. To reclaim memory on expired tickets, cache eviction policies are must be carefully configured to avoid memory creep. Disk offload and/or a more aggressive eviction could provide a suitable workaround.


### Troubleshooting Guidelines

* You will need to ensure that network communication across CAS nodes is allowed and no firewall or other component is blocking traffic. 

* If you are running this on a server with active firewalls, you will probably need to specify a fixed `remoteObjectPort`, within the `cacheManagerPeerListenerFactory`.
* Depending on environment settings and version of Ehcache used, you may also have to adjust the `shared` setting above.
* Ensure that each cache manager specified a name that matches the Ehcache configuration itself.
* You may also need to adjust your expiration policy to allow for a larger time span, specially for service tickets depending on network traffic and communication delay across CAS nodes particualrly in the event that a node is trying to join the cluster.
