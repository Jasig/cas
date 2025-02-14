---
layout: default
title: CAS - Monitoring
---

# CAS Monitoring

CAS monitors may be defined to report back the health status of the ticket registry and other underlying connections to systems that are in use by CAS. Spring Boot offers a number of monitors known as `HealthIndicator`s that are activated given the presence of specific settings (i.e. `spring.mail.*`). CAS itself providers a number of other monitors based on the same component that are listed below, whose action may require a combination of a particular dependency module and its relevant settings.

## Default

The default monitors report back brief memory and ticket stats. There is nothing more for you to do.
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#monitoring)
and [this guide](Configuration-Properties.html#memory).

<div class="alert alert-warning"><strong>YMMV</strong><p>In order to accurately and reliably report on ticket statistics, you are at the mercy of the underlying ticket registry to support the behavior in a performant manner which means that the infrastructure and network capabilities and latencies must be considered and carefully tuned. This might have become specially relevant in clustered deployments as depending on the ticket registry of choice, CAS may need to <i>interrogate</i> the entire cluster by running distributed queries to calculate ticket usage.</p></div>

## Memcached

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-memcached-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#memcached-monitors).

The actual memcached implementation may be supported via one of the following options, expected to be defined in the overlay.

###  Spymemcached

Enable support via the [spymemcached library](https://code.google.com/p/spymemcached/). 

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-memcached-spy</artifactId>
    <version>${cas.version}</version>
</dependency>
```

### AWS ElastiCache

For clusters running the Memcached engine, ElastiCache supports Auto Discovery—the ability 
for client programs to automatically identify all of the nodes in a cache cluster, 
and to initiate and maintain connections to all of these nodes. 

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-memcached-aws-elasticache</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Ehcache

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-ehcache-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#cache-monitors).

## MongoDb

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-mongo-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#mongodb-monitors).



## Hazelcast

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-hazelcast-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#cache-monitors).

## JDBC

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jdbc-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#database-monitoring).

## LDAP

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-ldap-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#ldap-connection-pool).
