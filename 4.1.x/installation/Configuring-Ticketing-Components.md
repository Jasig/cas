---
layout: default
title: CAS - Configuring Ticketing Components
---

# Configuring Ticketing Components
There are two core configurable ticketing components:

* `TicketRegistry` - Provides for durable ticket storage.
* `ExpirationPolicy` - Provides a policy framework for ticket expiration semantics.


## Ticket Registry
The deployment environment and technology expertise generally determine the particular `TicketRegistry` component.
A cache-backed implementation is recommended for HA deployments, while the default `DefaultTicketRegistry` in-memory component may be suitable for small deployments.


### Default (In-Memory) Ticket Registry
`DefaultTicketRegistry` uses a `ConcurrentHashMap` for memory-backed ticket storage and retrieval.
This component does not preserve ticket state across restarts. There are a few configuration knobs available:

* `initialCapacity` - `ConcurrentHashMap` initial capacity.
* `loadFactor` - `ConcurrentHashMap` load factor.
* `concurrencyLevel` - Allows tuning the `ConcurrentHashMap` for concurrent write support.

All three arguments map to those of the [`ConcurrentHashMap` constructor](http://goo.gl/qKKg7).
{% highlight xml %}
<bean id="ticketRegistry"
      class="org.jasig.cas.ticket.registry.DefaultTicketRegistry"
      c:initialCapacity="10000"
      c:loadFactor="1"
      c:concurrencyLevel="20" />
{% endhighlight %}

### Cache-Based Ticket Registries
Cached-based ticket registries provide a high-performance solution for ticket storage in high availability
deployments. Components for the following caching technologies are provided:

* [Hazelcast](Hazelcast-Ticket-Registry.html)
* [Ehcache](Ehcache-Ticket-Registry.html)
* [JBoss Cache](JBoss-Cache-Ticket-Registry.html)
* [Memcached](Memcached-Ticket-Registry.html)


### RDBMS Ticket Registries
RDBMS-based ticket registries provide a distributed ticket store across multiple CAS nodes. Components for the following caching technologies are provided:

* [JPA](JPA-Ticket-Registry.html)


### Ticket Generators
CAS presents a pluggable architecture for generating unique ticket ids for each ticket type. The configuration of each generator is defined at `src\main\webapp\WEB-INF\spring-configuration\uniqueIdGenerators.xml`. Here's a brief sample:

{% highlight xml %}

<bean id="ticketGrantingTicketUniqueIdGenerator" class="org.jasig.cas.util.DefaultUniqueTicketIdGenerator"
        c:maxLength="50" c:suffix="${host.name}" />

<bean id="serviceTicketUniqueIdGenerator" class="org.jasig.cas.util.DefaultUniqueTicketIdGenerator"
    c:maxLength="20" c:suffix="${host.name}" />

<bean id="loginTicketUniqueIdGenerator" class="org.jasig.cas.util.DefaultUniqueTicketIdGenerator"
    c:maxLength="30" c:suffix="${host.name}" />

<bean id="proxy20TicketUniqueIdGenerator" class="org.jasig.cas.util.DefaultUniqueTicketIdGenerator"
    c:maxLength="20" c:suffix="${host.name}" />

<util:map id="uniqueIdGeneratorsMap">
    <entry
        key="org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl"
        value-ref="serviceTicketUniqueIdGenerator" />
</util:map>

{% endhighlight %}

#### Components


##### `UniqueTicketIdGenerator`
Strategy parent interface that describes operations needed to generate a unique id for a ticket.

##### `DefaultUniqueTicketIdGenerator`
Uses numeric and random string generators to create a unique id, while supporting prefixes for each ticket type, as is outlined by the CAS protocol, as well as a suffix that typically is mapped to the CAS server node identifier in order to indicate which node is the author of this ticket. The latter configuration point helps with troubleshooting and diagnostics in a clustered CAS environment.

##### `HostNameBasedUniqueTicketIdGenerator`
An extension of `DefaultUniqueTicketIdGenerator` that is able auto-configure the suffix based on the underlying host name.
In order to assist with multi-node deployments, in scenarios where CAS configuration
and specially `cas.properties` file is externalized, it would be ideal to simply just have one set
of configuration files for all nodes, such that there would for instance be one `cas.properties` file
for all nodes. This would remove the need to copy/sync configuration files over across nodes, again in a
situation where they are externalized.

The drawback is that in keeping only one `cas.properties` file, we'd lose the ability
to define unique `host.name` property values for each node as the suffix, which would assist with troubleshooting
and diagnostics. To provide a remedy, this ticket generator is able to retrieve the `host.name` value directly from
the actual node name, rather than relying on the configuration, only if one isn't specified in
the `cas.properties` file.

##### `SamlCompliantUniqueTicketIdGenerator`
Unique Ticket Id Generator compliant with the SAML 1.1 specification for artifacts, that is also compliant with the SAML v2 specification.


### Ticket Registry Cleaner
The ticket registry cleaner should be used for ticket registries that cannot manage their own state. That would include the default in-memory registry and the JPA ticket registry. Cache-based ticket registry implementations such as Memcached, Hazelcast or Ehcache do not require a registry cleaner. The ticket registry cleaner configuration is specified in the `spring-configuration/ticketRegistry.xml` file.


#### Components

##### `RegistryCleaner`
Strategy interface to denote the start of cleaning the registry.


##### `DefaultTicketRegistryCleaner`
The default ticket registry cleaner scans the entire CAS ticket registry for expired tickets and removes them.  This process is only required so that the size of the ticket registry will not grow beyond a reasonable size.
The functionality of CAS is not dependent on a ticket being removed as soon as it is expired. Locking strategies may be used to support high availability environments. In a clustered CAS environment with several CAS nodes executing ticket cleanup, it is desirable to execute cleanup from only one CAS node at a time.


##### `LockingStrategy`
Strategy pattern for defining a locking strategy in support of exclusive execution of some process.


##### `NoOpLockingStrategy`
No-Op locking strategy that allows the use of `DefaultTicketRegistryCleaner` in environments where exclusive access to the registry for cleaning is either unnecessary or not possible.

#### Configuration
If you're using the default ticket registry configuration, your `/cas-server-webapp/WEB-INF/spring-configuration/ticketRegistry.xml` probably looks like this:

{% highlight xml %}
<!-- TICKET REGISTRY CLEANER -->
<bean id="ticketRegistryCleaner" 
  class="org.jasig.cas.ticket.registry.support.DefaultTicketRegistryCleaner"
      c:centralAuthenticationService-ref="centralAuthenticationService"
      c:ticketRegistry-ref="ticketRegistry"/>

<bean id="jobDetailTicketRegistryCleaner"  
      class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean"
    p:targetObject-ref="ticketRegistryCleaner"
    p:targetMethod="clean" />

<bean id="triggerJobDetailTicketRegistryCleaner" 
      class="org.springframework.scheduling.quartz.SimpleTriggerFactoryBean"
    p:jobDetail-ref="jobDetailTicketRegistryCleaner"
    p:startDelay="20000"
    p:repeatInterval="5000000" />
{% endhighlight %}



## Ticket Expiration Policies
CAS supports a pluggable and extensible policy framework to control the expiration policy of ticket-granting tickets (TGT) and service tickets (ST). [See this guide](Configuring-Ticket-Expiration-Policy.html) for details on how to configure the expiration policies.
