---
layout: default
title: CAS - Monitoring
category: Monitoring & Statistics
---

{% include variables.html %}

# CAS Monitoring

CAS monitors may be defined to report back the health status of the ticket registry 
and other underlying connections to systems that are in use by CAS. Spring Boot 
offers a number of monitors known as `HealthIndicator`s that are activated given 
the presence of specific settings (i.e. `spring.mail.*`). CAS itself providers a 
number of other monitors based on the same component that are listed below, whose 
action may require a combination of a particular dependency module and its relevant settings.

## Default

The default monitors report back brief memory and ticket stats.

Support is enabled by including the following dependency in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-core-monitor" %}

{% include {{ version }}/tgt-monitoring-configuration.md %}
{% include {{ version }}/st-monitoring-configuration.md %}
{% include {{ version }}/load-monitoring-configuration.md %}
{% include {{ version }}/memory-monitoring-configuration.md %}

<div class="alert alert-warning"><strong>YMMV</strong><p>In order to accurately and reliably 
report on ticket statistics, you are at the mercy of the underlying ticket registry to support 
the behavior in a performant manner which means that the infrastructure and network capabilities 
and latencies must be considered and carefully tuned. This might have become specially relevant 
in clustered deployments as depending on the ticket registry of choice, CAS may need 
to <i>interrogate</i> the entire cluster by running distributed queries to calculate ticket usage.</p></div>

## Memcached

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-memcached-monitor" %}

{% include {{ version }}/memcached-configuration.md configKey="cas.monitor.memcached" %}

The actual memcached implementation may be supported via one of the following options, expected to be defined in the overlay.

### Spymemcached

Enable support via the [spymemcached library](https://code.google.com/p/spymemcached/). 

Support is enabled by including the following dependency in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-memcached-spy" %}

### AWS ElastiCache

For clusters running the Memcached engine, ElastiCache supports Auto Discovery—the ability 
for client programs to automatically identify all of the nodes in a cache cluster, 
and to initiate and maintain connections to all of these nodes. 

Support is enabled by including the following dependency in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-memcached-aws-elasticache" %}

## Ehcache

Monitor the status and state of a cache backed by Ehcache.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-ehcache-monitor" %}

{% include {{ version }}/cache-monitoring-configuration.md %}

## MongoDb

Monitor the status and availability of a MongoDb database.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-mongo-monitor" %}

{% include {{ version }}/mongodb-configuration.md configKey="cas.monitor" %}

## Hazelcast

Monitor the status and state of a cache backed by Hazelcast.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-hazelcast-monitor" %}

{% include {{ version }}/cache-monitoring-configuration.md %}

## JDBC

Monitor the status and availability of a relational SQL database.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-jdbc-monitor" %}

{% include {{ version }}/jdbc-monitoring-configuration.md %}

## LDAP

Monitor the status and availability of an LDAP server.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-ldap-monitor" %}

{% include {{ version }}/ldap-monitoring-configuration.md %}
